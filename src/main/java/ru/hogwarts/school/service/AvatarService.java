package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;


@Service
public class AvatarService {
    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;
    @Value("${path.to.avatars.folder}")
    private Path avatarPath;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Avatar getById(Long id) {
        logger.info("Method started - 'getById'");
        return avatarRepository.findById(id).orElseThrow();
    }

    public Long save(Long studentId, MultipartFile multipartFile) throws IOException {
        logger.info("Method started - 'save'");
        logger.debug("File size = " + multipartFile.getSize());
        Files.createDirectories(avatarPath);
        int doIndex = multipartFile.getOriginalFilename().lastIndexOf(".");
        String fileExtension = multipartFile.getOriginalFilename().substring(doIndex + 1);
        Path path = avatarPath.resolve(studentId + "." + fileExtension);
        byte[] data = multipartFile.getBytes();
        Files.write(path, data, StandardOpenOption.CREATE);

        Student studentReference =(studentRepository.getReferenceById(studentId));
        Avatar avatar = avatarRepository.findByStudentId(studentReference.getId()).orElse(new Avatar());
        avatar.setStudent(studentReference);
        avatar.setMediaType(multipartFile.getContentType());
        avatar.setFileSize(multipartFile.getSize());
        avatar.setData(data);
        avatar.setFilePath(path.toAbsolutePath().toString());
        avatarRepository.save(avatar);
        return avatar.getId();
    }

    public List<Avatar> findAvatarsPaginated(Integer pageNumber, Integer pageSize ) {
        logger.info("Method started - 'findAvatarsPaginated'");
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return avatarRepository.findAll(pageRequest).getContent();
    }
}
