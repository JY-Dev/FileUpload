## Database RUN Docker
### CLI Command
- cd docker/mariadb/run.sh
- chmod +x run.sh.sh
- sudo ./run.sh

Kotlin과 MultiModule을 적용한 Spring Boot Application 작성에 익숙해지기 위해 FileUpload 기능을 각 레이어(Presentation, Application, Domain)로 나눠서 해당 조건을 기반으로 설계를 연습하기 위해 작성한 프로젝트 입니다. 

## Domain Layer

도메인 레이어에서는 비즈니스 도메인과 관련된 주요 로직과 규칙을 포함하는 곳입니다. 비즈니스 영역의 복잡성을 모델링하고 해결하기 위한 핵심 역할을 수행합니다. 

FileUpload와 같은 기능은 미디어 파일을 저장하는 기능이 될 수도 있고 엑셀같은 파일을 저장할 수 도 있습니다. 이번에 만들 기능은 미디어 파일을 저장하고 삭제하는 기능을 만들것 이기떄문에  Media라는 도메인에서 File이라는 세부 Domain을 정의하였습니다. 모듈과 패키지는 아래와 같이 구성했습니다.

```jsx
domain 모듈
├── media 모듈
│   ├── file 패키지
│   │   ├── ...
│   │   └── ...
│   └── ...
├── ...
```

그러면 FileUpload와 관련된 도메인 모델은 어떤게 필요할까요? 일단 파일은 S3나 Local 같은 저장소에 저장이 될것이고 해당 저장소에 저장된 후 해당 파일에 대한 메타데이터도 어딘가에 저장을 해야합니다. 그러면 일단 FileMetaData 모델과 저장소에 저장하기 위한 정보들을 처리하는 모델들이 필요할 것 같습니다. 핵심 클래스에 대해 알아보도록 하겠습니다.

### MediaFileMetaData 코드

```kotlin
@Entity
@Table(name = "MEDIA_FILE_META_DATA")
class MediaFileMetaData(
    storageType: StorageType,
    filePath : String,
    originalFileName: String,
    fileSize: Long,
    fileUrl: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    val id: Long = 0L

    @Enumerated(EnumType.STRING)
    @Column(name = "STORAGE_TYPE", nullable = false)
    var storageType: StorageType = storageType
        private set

    @Column(name = "FILE_SIZE", nullable = false)
    var fileSize: Long = fileSize
        private set

    @Column(name = "FILE_PATH", nullable = false)
    var filePath: String = filePath
        private set

    @Column(name = "ORIGINAL_FILE_NAME", nullable = false)
    var originalFileName: String = originalFileName
        private set

    @Column(name = "FILE_URL", unique = true, nullable = false)
    var fileUrl: String = fileUrl
        private set
}
```

MediaFileMetaData는 File에 대한 메타데이터 클래스입니다.

### MediaFileMetaData에서 사용된 도메인 모델

- StorageType: 어떤 저장소에 저장되었는지 구분하기 위한 정보
- FileSize : 파일에 대한 사이즈
- FIlePath : 파일 경로 EX) local/{TimeStamp}{UUID}.{FileExtension}
- OriginalFileName : 저장소에 저장할 때는 새로운 파일 경로와 새로운 파일 이름으로 저장되기 때문에 요청으로 받은 원본파일에 대한 이름
- FileUrl: 파일에 대한 URL

저장소에 저장하기 위해서는 저장소 구성 데이터가 필요합니다. 가령 S3에 저장한다고 하면 S3Bucket, Local인 경우 StoragePath 이러한 저장소 구성 데이터는 서비스 어플리케이션에 따라 설정이 달라지기 때문에 이러한 데이터를 받기 위해서는 적절한 데이터 모델이 필요합니다. 그래서 저는 아래와 같이 구성했습니다.

### StorageConfiguration 코드

```kotlin
sealed class StorageConfiguration(val baseUrl : String) {
    data class S3(val s3Bucket : String, val cloudFrontUrl : String) : StorageConfiguration(cloudFrontUrl)
    data class Local(val storagePath : String, val localUrl : String) : StorageConfiguration(localUrl)
}
```

StorageConfiguration는 저장소 구성 데이터 클래스입니다.

### StorageConfiguration에서 사용된 도메인 모델

- BaseUrl : 미디어 파일에 대한 BaseUrl
- S3 : S3 저장소에 대한 데이터
    - S3Bucket : AWS S3의 Bucket
    - CloudFrontUrl : S3랑 연동되어있는 CloudFrontUrl - BaseUrl
- Loca: Local 저장소에 대한 데이터
    - StorePath : 저장할 파일 경로
    - LocalUrl :  Local 파일에 접근할 수 있는 URL EX(서버 URL 등등) - BaseUrl

저는 해당 도메인을 사용하는 사용자 입장에서 어느 Repository를 통해서 저장해야하지? 라는 생각을 하지 않도록 MediaFileProcessor를 두어 행위를 나타내는 MediaFileAction, MediaFileRepository와 MediaFileConfiguration을 가진 FileStrategy를 통해 어느 Repository를 통해서 저장할지에 대한 로직을 위임했습니다. 

### MediaFileAction 코드

```kotlin
sealed class MediaFileAction {

    data class StoreMediaFile(
            val command: FileStrategy.StoreFileCommand
    ) : MediaFileAction()

    data class DeleteMediaFile(
            val command: FileStrategy.DeleteFileCommand
    ) : MediaFileAction()
}
```

MediaFileAction은 저장소의 동작을 정의한 클래스입니다. 

저장하고 삭제만 할 것이기 때문에 저장과 관련된 액션과 삭제와 관련된 액션을 정의하였습니다. 각 액션마다 필요한 데이터가 다르기 때문에 sealed class를 통해 분리해서 각각 필요한 데이터를 받을 수 있게 정의하였습니다.

### MediaFileAction에서 사용된 도메인 모델

- StoreMediaFile : 미디어 파일 저장 액션
- StoreFileCommand : 미디어 파일을 저장하기 위해 필요한 데이터 클래스
- DeleteMediaFile : 미디어 파일 삭제 액션
- DeleteFileCommand : 미디어 파일을 삭제하기 위해 필요한 데이터 클래스

### MediaFileRepository 코드

```kotlin
interface MediaFileRepository {
    val storageConfigClass: Class<out StorageConfiguration>

    fun save(
        storageConfiguration: StorageConfiguration,
        command : FileStrategy.StoreFileCommand
    )

    fun delete(
        storageConfiguration: StorageConfiguration,
        command : FileStrategy.DeleteFileCommand
    )

    fun resolveConfiguration(configuration : StorageConfiguration) : Boolean {
        return storageConfigClass.isInstance(configuration)
    }

    fun <T : StorageConfiguration> castConfiguration(configuration: StorageConfiguration, castConfiguration: Class<T>): T {
        if (!castConfiguration.isInstance(configuration)) {
            throw IllegalArgumentException("StorageConfiguration type mismatch")
        }
        return castConfiguration.cast(configuration)
    }
}
```

MediaFileRepository은 저장소에 대한 인터페이스 입니다.

### MediaFileRepository에서 사용된 도메인 모델

- StorageConfigClass : StorageConfiguration에 대한 클래스의 메타데이터 정보 resolveConfiguration 에서 사용하기 때문에 정의
- Save Method : 미디어 파일을 저장하는 로직
- Delete Method : 미디어 파일을 삭제하는 로직
- ResolveConfiguration Method : 해당 Repository가 인자로 받은 StorageConfiguration을 처리할 수 있는지 확인하는 로직

CastConfiguration Method : MediaFileRepository로 저장소들을 추상화했기 떄문에 StorageConfiguration도 구체적인 클래스에 대해 알지 못해 필요한 정보를 가져오지 못하기 때문에 저장소에 맞는 StorageConfiguration Casting해서 반환해주는 로직

### FileStrategy 코드

```kotlin
class MediaFileStrategy internal constructor(
    private val fileRepository: MediaFileRepository,
    private val storageConfiguration: StorageConfiguration
) : FileStrategy {

    override fun save(command: FileStrategy.StoreFileCommand) {
        fileRepository.save(
            storageConfiguration = storageConfiguration,
            command = command
        )
    }

    override fun delete(command: FileStrategy.DeleteFileCommand) {
        fileRepository.delete(
            storageConfiguration = storageConfiguration,
            command = command
        )
    }

}
```

FileStrategy은 파일 저장소에 대한 동작을 위임하는 클래스입니다.

인자로 MediaFileRepository와 StorageConfiguration을 받습니다. 그래서 이것들을 통해 FileRepository의 로직을 위임해서 처리합니다. 이렇게 위임함으로써 MediaFileRepository하고 StorageConfiguration이 늘어나도 별도로 수정하지 않아도 되기 때문에 책임분리를 할 수 있습니다. MediaFileStrategy는 MediaFileProcessorFactory에서 생성해줘야하기 때문에 다른 모듈에서 생성하지 않도록 internal 접근제어자를 사용했습니다.

### MediaFileStrategy에서 사용된 도메인 모델

- MediaFileRepository : 미디어 파일 저장소
- StorageConfiguration : 저장소 구성 정보
- Save Method : 파일 저장 (FileRepository의 저장로직 위임)
- Delete Method : 파일 삭제 (FileRepository의 삭제로직 위임)

### MediaFileProcess 코드

```kotlin
class MediaFileProcessor internal constructor(
    private val action: MediaFileAction,
    private val fileStrategy: FileStrategy
) {
    fun executeAction() {
        when(action) {
            is MediaFileAction.StoreMediaFile -> {
                fileStrategy.save(command = action.command)
            }

            is MediaFileAction.DeleteMediaFile -> {
                fileStrategy.delete(command = action.command)
            }
        }
    }
}
```

MediaFileProcess는 사용자가 실제로 저장소에 대한 동작을 실행시키기 위한 클래스입니다. 

MediaFileProccessor를 생성하는 책임을 MediaFileProccessorFactory로 위임 시킬 것이기 때문에 다른 모듈에서 해당 프로세서를 직접 생성하지 못하게 internal 접근 제어자로 선언하였고 executeAction 메서드를 통해 인자로 받은 Action을 FileStrategy를 통해 처리하게 됩니다.

### MediaFileProcess에서 사용된 도메인 모델

- MediaFileAction : MediaFile을 처리하는 Action
- FileStrategy :  파일 저장소에 대한 동작을 위임하는 클래스
- ExecuteAction Method : Action을 실행하는 메서드

### MediaFileProcessorFactory 코드

```kotlin
class MediaFileProcessorFactory(
        private val fileRepositoryResolver: FileRepositoryResolver
) {

    fun create(
        mediaFileAction: MediaFileAction,
        storageConfiguration: StorageConfiguration
    ): MediaFileProcessor {

        val repository = fileRepositoryResolver.resolve(storageConfiguration)

        val mediaFileStrategy = MediaFileStrategy(
                fileRepository = repository,
                storageConfiguration = storageConfiguration
        )

        return MediaFileProcessor(
                action = mediaFileAction,
                fileStrategy = mediaFileStrategy
        )
    }
}
```

MediaFileProcessorFactory는 MediaFileProcessor를 생성하기 위한 클래스입니다.

사용자는 MediaFileProcessorFactory의 create method를 통해 Processor를 생성할 수 있습니다. create method를 보면 먼저 Repository를 찾고 MediaFileStrategy를 만들어줍니다. 이걸 통해 MediaFileProcessor를 생성해 반환합니다.

### MediaFileProcessorFactory에서 사용된 도메인 모델

- FileRepositoryResolver : FileRepository를 가져오기 위한 책임을 가지고 있는 클래스
- MediaFileAction : MediaFile을 처리하는 Action
- StorageConfiguration : 저장소 구성 정보
- MediaFileRepository : 미디어 파일 저장소
- FileStrategy :  파일 저장소에 대한 동작을 위임하는 클래스
- MediaFileProcessor : 사용자가 실제로 저장소에 대한 동작을 실행시키기 위한 클래스

### MediaFileRepositoryResolver 코드

```kotlin
class MediaFileRepositoryResolver(
        private val fileRepositories : List<MediaFileRepository>
) {

    fun resolve(storageConfiguration : StorageConfiguration): MediaFileRepository {

        val resolveFileRepositoryPredicate: (MediaFileRepository) -> Boolean = { fileRepository ->
            fileRepository.resolveConfiguration(
                    configuration = storageConfiguration
            )
        }

        return fileRepositories.find(resolveFileRepositoryPredicate)
                ?: throw IllegalArgumentException("Can't resolve storageConfiguration for FileRepository")
    }
}
```

MediaFileRepositoryResolver는 MediaFileRepository를 반환하는 클래스입니다.

MediaFileRepository를 어떻게 찾을 수 있는지 로직을 보면 StorageConfiguration을 해당 Repository가 처리할 수 있는지 resolveConfiguration을 통해 확인하고 있으면 그걸 반환해줍니다.

### MediaFileRepositoryResolver에서 사용된 도메인 모델

- MediaFileRepository : 미디어 파일 저장소
- StorageConfiguration : 저장소 구성 정보

## Infrastructure

위에서는 도메인을 살펴봤는데요 이번에는 Infrastructure에 대해 살펴보도록 하겠습니다. 

저는 아래와 같이 편의상 별도의 모듈로 두진 않고 file 패키지안에 infra 패키지로 구성하였습니다.

```kotlin
domain 모듈
├── media 모듈
│   ├── file 패키지
│   │   ├── infra 패키지
│   │   └── ...
│   └── ...
├── ...
```

그러면 Infrasturcture에는 무엇이 있어야할까요? MediaFileRepository의 구현체가 있어야할텐데 Local 저장소와 S3 저장소 둘다 다루면 글이 길어지기 때문에 Local 저장소만 다루도록 하겠습니다.

### LocalFileRepository 코드

```kotlin
@Repository
class LocalFileRepository : MediaFileRepository {

    private val localConfigClass = StorageConfiguration.Local::class.java
    override val storageConfigClass: Class<out StorageConfiguration> = localConfigClass

    override fun save(storageConfiguration: StorageConfiguration, command: FileStrategy.StoreFileCommand) {

        validateStoreFile(command)

        val config = castConfiguration(storageConfiguration, localConfigClass)

        val filePath = Path(config.storagePath).resolve(command.filePath)
                .normalize()

        try {
            Files.createDirectories(filePath)
            Files.copy(command.fileInputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
        } catch (exception: IOException) {

            val deleteCommand = FileStrategy.DeleteFileCommand(command.filePath)
            delete(storageConfiguration, deleteCommand)

            throw IllegalArgumentException(
                    "Store file for local fail : FileName [%s]".format(command.filePath, exception)
            )
        }
    }

    override fun delete(storageConfiguration: StorageConfiguration, command: FileStrategy.DeleteFileCommand) {

        val config = castConfiguration(storageConfiguration, localConfigClass)

        try {
            val filePath = Path(config.storagePath).resolve(command.filePath)
                    .normalize()
            Files.deleteIfExists(filePath)
        } catch (e: IOException) {
            throw IllegalArgumentException(
                    "Delete file for local fail : FileName [%s]".format(command.filePath)
            )
        }
    }

    private fun validateStoreFile(command: FileStrategy.StoreFileCommand) {

        val filePath: String = command.filePath

        if (filePath.contains("..")) {
            throw IllegalArgumentException("Invalid store file for Local")
        }
    }
}
```

코드가 엄청 긴데 위에서 부터 한번 보겠습니다.

```kotlin
private val localConfigClass = StorageConfiguration.Local::class.java
override val storageConfigClass: Class<out StorageConfiguration> = localConfigClass
```

위 두 프로퍼티 같은 경우는 StorageConfiguration에 대해 검증하고 저장소에 맞는 구성파일을 가져오기 위해 사용되는 프로퍼티입니다.

```kotlin
override fun save(storageConfiguration: StorageConfiguration, command: FileStrategy.StoreFileCommand) {

        validateStoreFile(command)

        val config = castConfiguration(storageConfiguration, localConfigClass)

        val filePath = Path(config.storagePath).resolve(command.filePath)
                .normalize()

        try {
            Files.createDirectories(filePath)
            Files.copy(command.fileInputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
        } catch (exception: IOException) {

            val deleteCommand = FileStrategy.DeleteFileCommand(command.filePath)
            delete(storageConfiguration, deleteCommand)

            throw IllegalArgumentException(
                    "Store file for local fail : FileName [%s]".format(command.filePath, exception)
            )
        }
    }

private fun validateStoreFile(command: FileStrategy.StoreFileCommand) {

        val filePath: String = command.filePath

        if (filePath.contains("..")) {
            throw IllegalArgumentException("Invalid store file for Local")
        }
    }
```

- save 메서드에서 validateStoreFile 메서드를 통해 Local File에 저장할 때 문제가 없는지 검증을 해줍니다. 해당 검증 로직은 파일경로를 상위폴더로 지정할 수 없게 검증합니다.
- castConfiguration를 통해 StorageConfiguration.Local에 대한 정보를 가져옵니다
- 가져온 configuraiton으로 filePath를 구성합니다
- FilePath를 통해 디렉토리를 생성하고 파일을 저장합니다
- 만약 파일 저장에 실패했다면 저장하다가 만 파일이 남아있을테니 해당 파일을 지워주고 예외를 발생시킵니다

```kotlin
override fun delete(storageConfiguration: StorageConfiguration, command: FileStrategy.DeleteFileCommand) {

        val config = castConfiguration(storageConfiguration, localConfigClass)

        try {
            val filePath = Path(config.storagePath).resolve(command.filePath)
                    .normalize()
            Files.deleteIfExists(filePath)
        } catch (e: IOException) {
            throw IllegalArgumentException(
                    "Delete file for local fail : FileName [%s]".format(command.filePath)
            )
        }
    }
```

- castConfiguration를 통해 StorageConfiguration.Local에 대한 정보를 가져옵니다
- 가져온 configuraiton으로 filePath를 구성합니다
- 해당 Path에 있는 파일을 삭제합니다.
- 만약 삭제하지 못한다면 예외를 발생시킵니다.

## Application Layer

어플리케이션 레이어에서는 특정 어플리케이션 서비스에 관련한 비즈니스 로직과 비즈니스 규칙을 포함하는 곳입니다. Domain은 전체 어플리케이션과 관련된 비즈니스 로직과 비즈니스 규칙을 정의하지만 어플리케이션 레이어에서는 특정 어플리케이션 서비스에 특화된 비즈니스를 관리합니다.

모듈 구조는 아래와 같습니다.

```jsx
app 모듈
├── file-upload-api 모듈
│   ├── application 패키지
│   │   ├── file 패키지
│   │   └── ...
│   └── ...
├── ...
```

편의상 프레젠테이션 레이어와 어플리케이션 레이어를 하나의 app 모듈에 정의하고 패키지를 나눴습니다.

### StorageConfigurationResolver 코드

```kotlin
class StorageConfigurationResolver(
        private val s3MediaStorageProperties: S3MediaStorageProperties,
        private val localMediaStorageProperties: LocalMediaStorageProperties
) {

    fun resolve(storageType : StorageType) : StorageConfiguration = when (storageType) {
        StorageType.S3 -> StorageConfiguration.S3(s3MediaStorageProperties.bucket, s3MediaStorageProperties.baseUrl)
        StorageType.LOCAL -> StorageConfiguration.Local(localMediaStorageProperties.path, localMediaStorageProperties.baseUrl)
    }

}
```

StorageConfigurationResolver는 어플리케이션에 특화된 저장소 구성 데이터로 StorageType에 맞게 StorageConfiguration를 생성해주는 역할을 가지고 있습니다.

### UploadFileUseCase 코드

```kotlin
@Transactional
@Service
class UploadFileUseCase(
    private val processorFactory: MediaFileProcessorFactory,
    private val storageConfigurationResolver: StorageConfigurationResolver,
    private val fileMetaDataRepository: MediaFileMetaDataRepository
) {

    operator fun invoke(command: UploadFileCommand): UploadFileModel {
        val storageConfiguration = storageConfigurationResolver.resolve(command.storageType)
        val storeFileCommand = createStoreFileCommand(command)
        val storeAction = MediaFileAction.StoreMediaFile(storeFileCommand)

        processorFactory.create(storeAction, storageConfiguration)
            .executeAction()

        val fileUrl = constructFileURI(storageConfiguration.baseUrl, command.filePath).toString()

        val fileMetaData = MediaFileMetaData(
            storageType = command.storageType,
            originalFileName = command.originalFileName,
            fileSize = command.fileSize,
            fileUrl = fileUrl,
            filePath = command.filePath
        )

        fileMetaDataRepository.save(fileMetaData)

        return UploadFileModel(
            fileId = fileMetaData.id,
            fileUrl = fileUrl
        )
    }

    private fun createStoreFileCommand(command: UploadFileCommand) = FileStrategy.StoreFileCommand(
        fileInputStream = command.fileInputStream,
        filePath = command.filePath,
        fileContentType = command.fileContentType,
        fileSize = command.fileSize
    )

    private fun constructFileURI(baseUrl: String, filePath: String): URI = try {
        URI(baseUrl).resolve(filePath)
    } catch (exception: URISyntaxException) {
        throw IllegalArgumentException("URI Parsing Error URL : %s".format(baseUrl), exception)
    }
}
```

파일을 저장하기 위한 UseCase로 storageConfigurationResolver를 통해 StorageConfiguration을 가져오고 StoreAction을 생성해 ProcessorFactory를 통해 MediaFileProccessor를 가져옵니다. 

Processor에서 executeAction 메서드를 통해 파일 저장 액션을 실행합니다.

미디어 파일의 메타데이터인 MediaFileMetaData를 저장하고 id와 fileUrl을 반환해줍니다.

### DeleteFileUseCase 코드

```kotlin
@Transactional
@Service
class DeleteFileUseCase(
    private val processorFactory: MediaFileProcessorFactory,
    private val storageConfigurationResolver: StorageConfigurationResolver,
    private val fileMetaDataRepository: MediaFileMetaDataRepository
) {

    operator fun invoke(command: DeleteFileCommand) {

        val fileMetaData = fileMetaDataRepository.findByIdOrNull(command.fileId)
                ?: throw IllegalArgumentException("FileMetaData not exist for fileUrl")

        val storageConfiguration = storageConfigurationResolver.resolve(fileMetaData.storageType)
        val deleteCommand = FileStrategy.DeleteFileCommand(fileMetaData.filePath)
        val action = MediaFileAction.DeleteMediaFile(deleteCommand)

        processorFactory.create(action, storageConfiguration)
                .executeAction()

        fileMetaDataRepository.delete(fileMetaData)
    }
}
```

파일을 삭제하기 위한 UseCase로 파일을 삭제하기 위해 필요한 데이터를 가져오려고 FileMetaDataId로 FileMetadata를 조회하고 그걸 기반으로 StorageConfiguration를 StorageConfigurationResolver통해 가져오고 DeleteAction을 생성해 ProcessorFactory를 통해 MediaFileProccessor를 가져옵니다. 

Processor에서 executeAction 메서드를 통해 파일 삭제 액션을 실행합니다.

파일이 삭제되었다면 FileMetaData도 삭제합니다.

## Presentation Layer

프레젠테이션 레이어는 사용자와 애플리케이션의 비즈니스 로직 사이의 인터페이스 역할을 하며, 소프트웨어 설계의 기본 원칙인 관심사 분리를 보장합니다. 

모듈 구조는 아래와 같습니다.

```kotlin
app 모듈
├── file-upload-api 모듈
│   ├── presentation 패키지
│   │   ├── file 패키지
│   │   └── ...
│   └── ...
├── ...
```

### MultipartFilePathResolver 코드

```kotlin
class MultipartFilePathResolver(
        @Value("\${file.media.base-root}") private val baseRoot: String
) {

    fun resolve(file: MultipartFile): Path {
        val fileName = file.originalFilename?.let { originalName ->
            val uuid = UUID.randomUUID().toString()
            val fileExtension = StringUtils.getFilenameExtension(originalName)

            if (fileExtension != null) "$uuid.$fileExtension" else uuid
        } ?: throw IllegalArgumentException("Original filename must not be null")

        return Path.of(baseRoot).run {
            val currentDateString = LocalDate.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_DATE)

            this.resolve(currentDateString)
                    .resolve(abs(currentDateString.hashCode()).toString())
                    .resolve(fileName)
                    .normalize()
        }
    }
}
```

Spring Web에서는 파일 관련해서 MultipartFile이라는 클래스를 제공하는데 멀티파트파일에 대해서 Request를 받을 수 있습니다. Web 의존성이 아무래도 있기 때문에 다른 DTO로 변환해줘야합니다. 그래서 MultiPartFile에서 저장에 필요한 정보를 가져와 새로운 DTO로 변환해줘야합니다. 

MultipartFilePathResolver는 MultiPartFile으로 FilePath 즉 File에 대한 이름을 만들어주는 역할을 가진 클래스입니다. 일단 FileName은 겹치지 않게 UUID와 확장자로 만들고 Path도 겹치지 않도록 현재시간의 hash 값과 BaseRoot를 사용해서 path를 만듭니다.

### FileController 코드

```kotlin
@RestController
class FileController(
        private val multipartFilePathResolver: MultipartFilePathResolver,
        private val uploadFileUseCase : UploadFileUseCase,
        private val deleteFileUseCase : DeleteFileUseCase
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/files")
    fun uploadFile(file: MultipartFile): UploadFileResponse {

        val filePath = multipartFilePathResolver.resolve(file)
        val contentType = file.contentType ?: throw IllegalArgumentException("File content type not exist")
        val originalFileName = file.originalFilename?: throw IllegalArgumentException("File OriginalName not exist")

        val command = UploadFileCommand(
                fileInputStream = file.inputStream,
                filePath = filePath.toString(),
                originalFileName = originalFileName,
                fileContentType = contentType,
                fileSize = file.size,
                storageType = StorageType.LOCAL
        )

        val model = uploadFileUseCase(command)

        return UploadFileResponse(
                fileId = model.fileId,
                fileUrl = model.fileUrl
        )
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/files/{fileId}")
    fun deleteFile(@PathVariable fileId: Long) {

        val command = DeleteFileCommand(
                fileId = fileId
        )

        deleteFileUseCase.invoke(command)
    }
}
```

uploadFile 메서드는 MultipartFilePathResolver를 통해 FilePath를 가져오고 UploadFileCommand를 만들어 파일을 저장하는 유스케이스를 실행시켜 파일을 저장하고 Response로 FileId와 FileUrl을 반환합니다.

deleteFile 메서드는 FileId를 받아 DelteFileCommand를 만들어 파일을 삭제하는 유스케이스를 실행시켜 파일을 삭제합니다.
