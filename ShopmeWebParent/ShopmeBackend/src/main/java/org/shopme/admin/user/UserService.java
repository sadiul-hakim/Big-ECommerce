package org.shopme.admin.user;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.shopme.admin.util.FileUtil;
import org.shopme.common.entity.User;
import org.shopme.common.pojo.ChangePasswordPojo;
import org.shopme.common.pojo.PaginationResult;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.shopme.common.util.PageUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private static final int USER_LIST_LIMIT = 10;
    private static final String FILE_PATH = "/image/user/";
    private static final String DEFAULT_PHOTO_NAME = "default_user.svg";

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public JpaResult save(User user, MultipartFile file) {
        try {

            // Encode the password
            user.setPassword(encoder.encode(user.getPassword()));

            // Handle photo upload
            if (file == null || Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
                user.setPhoto(DEFAULT_PHOTO_NAME);
            } else {
                var filePath = FileUtil.uploadFile(file, FILE_PATH);
                if (!StringUtils.hasText(filePath)) {
                    log.warn("UserService.save :: Could not upload file!");
                    user.setPhoto(DEFAULT_PHOTO_NAME);
                } else {
                    user.setPhoto(filePath);
                }
            }

            // Check if user exists
            var existingUser = findByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                return new JpaResult(JpaResultType.NOT_UNIQUE, "User " + user.getEmail() + " already exists!");
            }

            var savedUser = userRepository.save(user);
            return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved user " + savedUser.getEmail());
        } catch (Exception ex) {
            log.error("UserService.save :: Error Occurred {}", ex.getMessage());
            return new JpaResult(JpaResultType.FAILED,
                    "Failed to save/update user: " + user.getEmail() + ". Please try again!");
        }
    }

    public JpaResult updateUser(User user, MultipartFile file) {

        var existingUserOptional = findByEmail(user.getEmail());
        if (existingUserOptional.isEmpty()) {
            return new JpaResult(JpaResultType.FAILED, "User " + user.getEmail() + " does not exist!");
        }

        var existingUser = existingUserOptional.get();
        if (file != null && StringUtils.hasText(file.getOriginalFilename())) {
            var filePath = FileUtil.uploadFile(file, FILE_PATH);
            if (!StringUtils.hasText(filePath)) {
                log.warn("UserService.update :: Could not upload file!");
            } else {
                FileUtil.deleteFile(FILE_PATH, existingUser.getPhoto());
                existingUser.setPhoto(filePath);
            }
        }

        existingUser.setFirstname(user.getFirstname());
        existingUser.setLastname(user.getLastname());
        existingUser.setEnabled(user.isEnabled());
        existingUser.setRoles(user.getRoles());

        var savedUser = userRepository.save(existingUser);
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully updated user " + savedUser.getEmail());
    }

    public JpaResult updatePassword(ChangePasswordPojo pojo, int userId) {

        if (!pojo.getNewPassword().equals(pojo.getConfirmPassword())) {
            return new JpaResult(JpaResultType.FAILED, "Confirm password does not match!");
        }

        var existingUserOptional = findById(userId);
        if (existingUserOptional.isEmpty()) {
            return new JpaResult(JpaResultType.FAILED, "User does not exist!");
        }

        var matches = encoder.matches(pojo.getCurrentPassword(), existingUserOptional.get().getPassword());
        if (!matches) {
            return new JpaResult(JpaResultType.FAILED, "Invalid Password!");
        }

        var user = existingUserOptional.get();
        user.setPassword(encoder.encode(pojo.getNewPassword()));
        var savedUser = userRepository.save(user);
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully updated user " + savedUser.getEmail());
    }

    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public PaginationResult findAllPaginated(int pageNumber) {
        var page = userRepository.findAll(PageRequest.of(pageNumber, USER_LIST_LIMIT));
        return PageUtil.prepareResult(page);
    }

    public PaginationResult searchUser(String text, int pageNum) {
        var page = userRepository.findAllByFirstnameContainingOrLastnameContainingOrEmailContaining(text, text, text, PageRequest.of(pageNum, 100));
        return PageUtil.prepareResult(page);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public JpaResult delete(int id) {
        Optional<User> user = findById(id);
        if (user.isEmpty()) {
            return new JpaResult(JpaResultType.NOT_FOUND, "User does not exists!");
        }
        FileUtil.deleteFile(FILE_PATH, user.get().getPhoto());
        userRepository.delete(user.get());
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully deleted user.");
    }



    public byte[] csvData() {
        var users = findAll();
        StringBuilder data = new StringBuilder("Id,First Name,Last Name,Email,Photo,Enables,Roles,Joined\n");
        for (var user : users) {
            data.append(user.getId())
                    .append(",")
                    .append(user.getFirstname())
                    .append(",")
                    .append(user.getLastname())
                    .append(",")
                    .append(user.getEmail())
                    .append(",")
                    .append(user.getPhoto())
                    .append(",")
                    .append(user.isEnabled())
                    .append(",")
                    .append(user.getRoles())
                    .append(",")
                    .append(user.getJoined())
                    .append("\n");
        }

        return data.toString().getBytes(StandardCharsets.UTF_8);
    }
}
