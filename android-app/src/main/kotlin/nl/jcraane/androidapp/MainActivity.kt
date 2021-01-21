package nl.jcraane.androidapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.project.Images

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.greetingFromCommon).text = "kmm-images"
        println("CONTEXT = " + ApplicationContext.context)
//        findViewById<ImageView>(R.id.image).setImageDrawable(Images.PIANO.drawable(this))
    }
}
