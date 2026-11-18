package com.example.supabse_actividad

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.supabse_actividad.data.SupabaseManager
import com.example.supabse_actividad.models.Alumno
import com.example.supabse_actividad.utils.SupabaseErrorHandler
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var txtAlumnos: TextView
    private lateinit var progressMaterias: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtAlumnos = findViewById(R.id.txtAlumnos)
        progressMaterias = findViewById(R.id.progressMaterias)

        findViewById<Button>(R.id.btnConsultar)
            .setOnClickListener { consultarAlumnos() }

        consultarAlumnos()
    }

    private fun consultarAlumnos() {
        lifecycleScope.launch {
            progressMaterias.visibility = View.VISIBLE
            try {
                val alumnos = SupabaseManager.client
                    .from("alumnos")
                    .select {
                        order("nombres", Order.ASCENDING)
                    }
                    .decodeList<Alumno>()

                txtAlumnos.text = if (alumnos.isEmpty()) {
                    "No se encontraron alumnos."
                } else {
                    alumnos.joinToString("\n\n") { a ->
                        "Nombre: ${a.nombres}\n" +
                                "Paralelo: ${a.paralelo}\n" +
                                "Correo: ${a.correo ?: "Sin registrar"}\n" +
                                "Teléfono: ${a.telefono ?: "Sin registrar"}"
                    }
                }
            } catch (e: Exception) {
                SupabaseErrorHandler.show(this@MainActivity, e)
            } finally {
                progressMaterias.visibility = View.GONE
            }
        }
    }
}
