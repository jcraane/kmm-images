package nl.jcraane.androidapp

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.project.Image

fun Image.drawable(context: Context): Drawable? {
    val id = context.resources.getIdentifier(this.name, "drawable", context.packageName)
    return if (id > 0) {
        ContextCompat.getDrawable(context, id)
    } else {
        null
    }
}
