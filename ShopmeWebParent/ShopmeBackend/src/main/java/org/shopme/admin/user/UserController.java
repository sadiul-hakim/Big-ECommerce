package org.shopme.admin.user;

import org.shopme.admin.role.RoleService;
import org.shopme.common.entity.User;
import org.shopme.common.pojo.ChangePasswordPojo;
import org.shopme.common.pojo.TableUrlPojo;
import org.shopme.common.util.JpaResultType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping
    public ModelAndView usersPage(@RequestParam(defaultValue = "0") int page, ModelAndView model) {
        var userResult = userService.findAllPaginated(page);
        model.addObject("userResult", userResult);

        var pageUrl = new TableUrlPojo("/users/search", "/users",
                "/users/export-csv", "/users/create");
        model.addObject("tableUrl", pageUrl);

        model.setViewName("users");

        return model;
    }

    @GetMapping("/search")
    public ModelAndView searchUsers(@RequestParam(defaultValue = "") String text, @RequestParam(defaultValue = "0") int page, ModelAndView model) {
        var userResult = userService.searchUser(text, page);
        model.addObject("userResult", userResult);

        var pageUrl = new TableUrlPojo("/users/search", "/users",
                "/users/export-csv", "/users/create");
        model.addObject("tableUrl", pageUrl);

        model.setViewName("users");

        return model;
    }

    @GetMapping("/create")
    public ModelAndView createUsersPage(ModelAndView model) {

        var roles = roleService.findAll();
        model.addObject("user", new User());
        model.addObject("roles", roles);
        model.setViewName("create_user");
        model.addObject("updatingUser", false);

        return model;
    }

    @PostMapping("/save")
    public ModelAndView save(
            @ModelAttribute User user,
            @RequestParam boolean updating,
            @RequestParam MultipartFile file,
            ModelAndView model
    ) {

        var result = updating ? userService.updateUser(user, file) : userService.save(user, file);
        model.addObject("savedSuccessfully", result.type().equals(JpaResultType.SUCCESSFUL));
        model.addObject("savingUser", true);
        model.addObject("message", result.message());

        var pageUrl = new TableUrlPojo("/users/search", "/users",
                "/users/export-csv", "/users/create");
        model.addObject("tableUrl", pageUrl);

        if (result.type().equals(JpaResultType.NOT_UNIQUE)) {
            var roles = roleService.findAll();
            model.addObject("user", user);
            model.addObject("roles", roles);
            model.setViewName("create_user");
        } else {
            var userResult = userService.findAllPaginated(0);
            model.addObject("userResult", userResult);
            model.setViewName("users");
        }
        return model;
    }

    @GetMapping("/update/{userId}")
    public ModelAndView updatePage(@PathVariable int userId, ModelAndView model) {

        var user = userService.findById(userId);
        if (user.isEmpty()) {
            var users = userService.findAll();
            var pageUrl = new TableUrlPojo("/users/search", "/users",
                    "/users/export-csv", "/users/create");
            model.addObject("tableUrl", pageUrl);
            model.addObject("users", users);
            model.setViewName("users");

            model.addObject("updatingUser", true);
            model.addObject("message", "User does not exists!");
            return model;
        }

        var roles = roleService.findAll();
        model.addObject("user", user.get());
        model.addObject("roles", roles);
        model.setViewName("create_user");
        model.addObject("updatingUser", true);

        return model;
    }

    @PostMapping("/updatePassword")
    public ModelAndView updatePassword(@ModelAttribute ChangePasswordPojo pojo, @RequestParam int userId,
                                       ModelAndView model) {
        var result = userService.updatePassword(pojo, userId);
        model.addObject("changedSuccessfully", result.type().equals(JpaResultType.SUCCESSFUL));
        model.addObject("changingPassword", true);
        model.addObject("message", result.message());
        model.addObject("pojo", result.type().equals(JpaResultType.SUCCESSFUL) ? new ChangePasswordPojo() : pojo);
        model.setViewName("changePassword");
        model.addObject("userId", userId);
        return model;
    }

    @GetMapping("/changePassword/{userId}")
    public ModelAndView changePasswordPage(@PathVariable int userId, ModelAndView model) {
        var userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            var users = userService.findAll();
            var pageUrl = new TableUrlPojo("/users/search", "/users",
                    "/users/export-csv", "/users/create");
            model.addObject("tableUrl", pageUrl);
            model.addObject("users", users);
            model.setViewName("users");

            model.addObject("updatingUser", true);
            model.addObject("message", "User does not exists!");
            return model;
        }

        model.addObject("userId", userId);
        model.setViewName("changePassword");
        model.addObject("pojo", new ChangePasswordPojo());

        return model;
    }

    @GetMapping("/delete/{id}")
    public ModelAndView deleteUser(@PathVariable int id, ModelAndView model) {
        var result = userService.delete(id);
        model.addObject("deletedSuccessfully", result.type().equals(JpaResultType.SUCCESSFUL));
        model.addObject("deletingUser", true);
        var pageUrl = new TableUrlPojo("/users/search", "/users",
                "/users/export-csv", "/users/create");
        model.addObject("tableUrl", pageUrl);
        model.addObject("message", result.message());

        var users = userService.findAllPaginated(0);
        model.addObject("userResult", users);
        model.setViewName("users");
        return model;
    }

    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exportCsv() {
        var data = userService.csvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
