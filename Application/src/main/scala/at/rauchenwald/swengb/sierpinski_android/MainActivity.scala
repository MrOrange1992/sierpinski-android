package at.rauchenwald.swengb.sierpinski_android

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.{Button, TextView}

/**
  * Created by felix on 04/12/2016.
  */
class MainActivity extends Activity
{
  private var sierpinskiCanvas: Sierpinski = null

  override protected def onCreate(savedInstanceState: Bundle)
  {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    sierpinskiCanvas = findViewById(R.id.signature_canvas).asInstanceOf[Sierpinski]

    val recDepthView = findViewById(R.id.txtViewRecDepth).asInstanceOf[TextView]

    recDepthView.setText("0")

    val button0 = findViewById(R.id.btn0).asInstanceOf[Button]
    val button1 = findViewById(R.id.btn1).asInstanceOf[Button]
    val button2 = findViewById(R.id.btn2).asInstanceOf[Button]
    val button3 = findViewById(R.id.btn3).asInstanceOf[Button]
    val button4 = findViewById(R.id.btn4).asInstanceOf[Button]
    val button5 = findViewById(R.id.btn5).asInstanceOf[Button]
    val button6 = findViewById(R.id.btn6).asInstanceOf[Button]
    val button7 = findViewById(R.id.btn7).asInstanceOf[Button]


    button0.setOnClickListener(new View.OnClickListener() {
      def onClick(v : View) {
        recDepthView.setText("0")
        sierpinskiCanvas.setRecursionDepth(0)
        sierpinskiCanvas.invalidate()
      }
    })
    button1.setOnClickListener(new View.OnClickListener() {
      def onClick(v : View) {
        recDepthView.setText("1")
        sierpinskiCanvas.setRecursionDepth(1)
        sierpinskiCanvas.invalidate()
      }
    })
    button2.setOnClickListener(new View.OnClickListener() {
      def onClick(v : View) {
        recDepthView.setText("2")
        sierpinskiCanvas.setRecursionDepth(2)
        sierpinskiCanvas.invalidate()
      }
    })
    button3.setOnClickListener(new View.OnClickListener() {
      def onClick(v : View) {
        recDepthView.setText("3")
        sierpinskiCanvas.setRecursionDepth(3)
        sierpinskiCanvas.invalidate()
      }
    })
    button4.setOnClickListener(new View.OnClickListener() {
      def onClick(v : View) {
        recDepthView.setText("4")
        sierpinskiCanvas.setRecursionDepth(4)
        sierpinskiCanvas.invalidate()
      }
    })
    button5.setOnClickListener(new View.OnClickListener() {
      def onClick(v : View) {
        recDepthView.setText("5")
        sierpinskiCanvas.setRecursionDepth(5)
        sierpinskiCanvas.invalidate()
      }
    })
    button6.setOnClickListener(new View.OnClickListener() {
      def onClick(v : View) {
        recDepthView.setText("6")
        sierpinskiCanvas.setRecursionDepth(6)
        sierpinskiCanvas.invalidate()
      }
    })
    button7.setOnClickListener(new View.OnClickListener() {
      def onClick(v : View) {
        recDepthView.setText("7")
        sierpinskiCanvas.setRecursionDepth(7)
        sierpinskiCanvas.invalidate()
      }
    })
  }
}
