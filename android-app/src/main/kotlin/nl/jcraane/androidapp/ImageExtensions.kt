package nl.jcraane.androidapp

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.project.Image

/**
 * Extension function to obtain a drawable from an Image.
 *
 * @param context The Android context which is used to obtain the resources from.
 * @return Drawable?
 */
fun Image.drawable(context: Context): Drawable? {
    val id = context.resources.getIdentifier(this.name, "drawable", context.packageName)
    return if (id > 0) {
        ContextCompat.getDrawable(context, id)
    } else {
        null
    }
}
