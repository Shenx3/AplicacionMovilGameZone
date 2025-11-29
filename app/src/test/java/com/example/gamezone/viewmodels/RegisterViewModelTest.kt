package com.example.gamezone.viewmodels

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias para la lógica de validación de RegisterViewModel.
 */
class RegisterViewModelTest {

    // --- SETUP BÁSICO ---

    /**
     * Crea un estado base que es válido para la mayoría de los campos.
     */
    private fun setupValidState(viewModel: RegisterViewModel) {
        viewModel.onNombreChange("Juan Perez")
        viewModel.onEmailChange("juan.perez@test.com")
        viewModel.onUsuarioChange("juan_gamer")
        // Contraseña segura: 8+ caracteres, mayús, minús, número
        viewModel.onContrasenaChange("Password123")
        viewModel.onConfirmarContrasenaChange("Password123")
        viewModel.onGeneroChange("RPG")
        viewModel.onTerminosChange(true)
    }


    // --- PRUEBAS DE ÉXITO ---

    @Test
    fun `test_01_validate_all_fields_valid_returns_true`() {
        // OBJETIVO: Verificar que el formulario es válido cuando todos los datos cumplen los requisitos.
        val viewModel = RegisterViewModel()
        setupValidState(viewModel)

        // El formulario debe ser válido
        assertTrue("La validación debe ser TRUE cuando todos los campos son válidos", viewModel.validate())

        // Y no debe haber errores específicos
        val errors = viewModel.errors.value
        assertTrue(errors.nombreCompleto == null)
        assertTrue(errors.email == null)
    }

    // --- PRUEBAS DE FALLO (Validación por Campo) ---

    @Test
    fun `test_02_validate_nombre_too_short_returns_false`() {
        // OBJETIVO: Verificar el requisito de longitud mínima para el nombre (min 3 caracteres).
        val viewModel = RegisterViewModel()
        setupValidState(viewModel)

        // 1. Invalidar campo: Nombre < 3 caracteres
        viewModel.onNombreChange("JP")

        // 2. Afirmar: La validación debe fallar.
        assertFalse("Debe ser FALSE si el nombre es muy corto", viewModel.validate())
        assertTrue("Debe haber un mensaje de error en nombreCompleto", viewModel.errors.value.nombreCompleto != null)
    }

    @Test
    fun `test_03_validate_email_invalid_format_returns_false`() {
        // OBJETIVO: Verificar la regex o patrón de correo electrónico (que tenga formato válido).
        val viewModel = RegisterViewModel()
        setupValidState(viewModel)

        // 1. Invalidar campo: Email sin @ o dominio
        viewModel.onEmailChange("emailinvalido.com")

        // 2. Afirmar: La validación debe fallar.
        assertFalse("Debe ser FALSE si el formato del email es inválido", viewModel.validate())
        assertTrue("Debe haber un mensaje de error en email", viewModel.errors.value.email != null)
    }

    @Test
    fun `test_04_validate_passwords_do_not_match_returns_false`() {
        // OBJETIVO: Verificar que la contraseña y su confirmación sean idénticas.
        val viewModel = RegisterViewModel()
        setupValidState(viewModel)

        // 1. Invalidar campo: Contraseñas diferentes
        viewModel.onContrasenaChange("Password123")
        viewModel.onConfirmarContrasenaChange("Different456")

        // 2. Afirmar: La validación debe fallar.
        assertFalse("Debe ser FALSE si las contraseñas no coinciden", viewModel.validate())
        assertTrue("Debe haber un mensaje de error en confirmarContrasena", viewModel.errors.value.confirmarContrasena != null)
    }

    @Test
    fun `test_05_validate_password_missing_uppercase_returns_false`() {
        // OBJETIVO: Verificar el requisito de complejidad (que la contraseña contenga mayúsculas).
        val viewModel = RegisterViewModel()
        setupValidState(viewModel)

        // 1. Invalidar campo: Faltan mayúsculas
        viewModel.onContrasenaChange("password123") // Solo minúsculas y número
        viewModel.onConfirmarContrasenaChange("password123")

        // 2. Afirmar: La validación debe fallar.
        assertFalse("Debe ser FALSE si la contraseña no tiene mayúsculas", viewModel.validate())
        assertTrue("Debe haber un mensaje de error en contrasena", viewModel.errors.value.contrasena != null)
    }

    @Test
    fun `test_06_validate_terms_not_accepted_returns_false`() {
        // OBJETIVO: Verificar que el checkbox de términos y condiciones es obligatorio.
        val viewModel = RegisterViewModel()
        setupValidState(viewModel)

        // 1. Invalidar campo: Términos no aceptados
        viewModel.onTerminosChange(false)

        // 2. Afirmar: La validación debe fallar.
        assertFalse("Debe ser FALSE si los términos no están aceptados", viewModel.validate())
        assertTrue("Debe haber un mensaje de error en aceptaTerminos", viewModel.errors.value.aceptaTerminos != null)
    }
}