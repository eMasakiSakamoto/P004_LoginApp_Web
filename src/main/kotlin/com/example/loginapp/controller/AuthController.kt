package com.example.loginapp.controller

import com.example.loginapp.entity.User
import com.example.loginapp.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class AuthController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    // ======================================
    // ログイン画面
    // ======================================
    @GetMapping("/login")
    fun loginPage(
        @RequestParam(required = false) error: Boolean?,
        @RequestParam(required = false) logout: Boolean?,
        model: Model
    ): String {
        if (error == true) {
            model.addAttribute("errorMessage", "ユーザー名またはパスワードが正しくありません")
        }
        if (logout == true) {
            model.addAttribute("logoutMessage", "ログアウトしました")
        }
        return "login"
    }

    // ======================================
    // アカウント登録画面
    // ======================================
    @GetMapping("/register")
    fun registerPage(): String {
        return "register"
    }

    @PostMapping("/register")
    fun registerUser(
        @RequestParam username: String,
        @RequestParam email: String,
        @RequestParam password: String,
        @RequestParam confirmPassword: String,
        redirectAttributes: RedirectAttributes,
        model: Model
    ): String {

        // --- バリデーション --- 
        val errors = mutableListOf<String>()

        if (username.isBlank() || username.length < 3) {
            errors.add("ユーザー名は3文字以上で入力してください")
        }
        if (email.isBlank() || !email.contains("@")) {
            errors.add("有効なメールアドレスを入力してください")
        }
        if (password.length < 6) {
            errors.add("パスワード」は6文字以上で入力してください")
        }
        if (password != confirmPassword) {
            errors.add("パスワードが一致しません")
        }
        if (userRepository.existsByUsername(username)) {
            errors.add("このユーザー名は既に使用されています")
        }
        if (userRepository.existsByEmail(email)) {
            errors.add("このメールアドレスは既に登録されています")
        }

        if (errors.isNotEmpty()) {
            model.addAttribute("errors", errors)
            model.addAttribute("username", username)
            model.addAttribute("email", "email")
            return "register"
        }

        // --- ユーザー保存 ---
        val user = User(
            username = username,
            email = email,
            password = passwordEncoder.encode(password)
        )
        userRepository.save(user)

        redirectAttributes.addFlashAttribute(
            "successMessage",
            "アカウントが作成されました。ログインしてください"
        )
        return "redirect:/login"
    }
}
