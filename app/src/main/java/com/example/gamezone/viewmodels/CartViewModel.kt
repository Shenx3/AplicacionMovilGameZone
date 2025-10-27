package com.example.gamezone.viewmodels

import androidx.lifecycle.ViewModel
import com.example.gamezone.data.CartItem
import com.example.gamezone.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.util.Locale

// Estado para el carrito
data class CartState(
    val items: List<CartItem> = emptyList(),
    val totalAmount: String = "$0"
)

class CartViewModel : ViewModel() {
    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state

    /**
     * Agrega un producto al carrito o incrementa su cantidad si ya existe.
     */
    fun addToCart(product: Product) {
        _state.update { currentState ->
            val existingItem = currentState.items.find { it.product.id == product.id }
            val updatedItems = if (existingItem != null) {
                // Producto ya existe, incrementar cantidad
                currentState.items.map {
                    if (it.product.id == product.id) it.copy(quantity = it.quantity + 1) else it
                }
            } else {
                // Nuevo producto, agregar a la lista
                currentState.items + CartItem(product = product, quantity = 1)
            }
            currentState.copy(items = updatedItems).calculateTotal()
        }
    }

    /**
     * Elimina un producto completamente del carrito.
     */
    fun removeFromCart(productId: Int) {
        _state.update { currentState ->
            val updatedItems = currentState.items.filter { it.product.id != productId }
            currentState.copy(items = updatedItems).calculateTotal()
        }
    }

    /**
     * Calcula el monto total y lo formatea a una String (simulación).
     * Nota: El formato es una simulación simple, asume que el precio tiene formato $XX.XXX.
     */
    private fun CartState.calculateTotal(): CartState {
        val total = this.items.sumOf { item ->
            // Limpia el precio del formato "$39.990" a un Double (ej: 39990.0)
            val priceCleaned = item.product.price.replace(Regex("[$.]"), "").replace(",", ".").toDoubleOrNull() ?: 0.0
            priceCleaned * item.quantity
        }

        // Formato simple de vuelta a "$XX.XXX"
        val simpleTotalFormat = "$${total.toInt().toString().replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1.")}"
        return this.copy(totalAmount = simpleTotalFormat)
    }

    /**
     * Vacia el carrito después de la compra.
     */
    fun clearCart() {
        _state.value = CartState()
    }
}