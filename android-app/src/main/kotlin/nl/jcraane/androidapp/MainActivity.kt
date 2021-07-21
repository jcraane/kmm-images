package nl.jcraane.androidapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.project.Images
import com.example.project.MainViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.greetingFromCommon).text = "kmm-images"
        println("CONTEXT = " + ApplicationContext.context)
        findViewById<ImageView>(R.id.image).setImageDrawable(Images.PIANO.drawable(this))

        // example of using images from viewmodel which resides in shared module
        val viewModel = MainViewModel()
        val viewState = viewModel.createViewState()
        findViewById<ImageView>(R.id.iconFromViewModel).setImageDrawable(viewState.image.drawable(this))

        findViewById<TextView>(R.id.titleFromViewModel).text = viewState.title
    }
}
