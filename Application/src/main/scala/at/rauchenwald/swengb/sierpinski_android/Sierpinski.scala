package at.rauchenwald.swengb.sierpinski_android

import android.content.Context
import android.graphics._
import android.util.AttributeSet
import android.view.View


/*--------------------------------------------------------------------------------------------------

                                            top
                                  X----------X----------X
                                   \                   /
                                    \                 /
                                     \               /
                                      \     TOP     /
                                       X           X
                                        \         /
                                         \       /
                                          \     /
                                           \   /
                                            \ /
             leftOuter X---------------------X---------------------X rightOuter
                        \                                         /
                         \                                       /
                          \                                     /
                           \                                   /
                            \                                 /
                             \              BASE             /
                              \                             /
                               \                           /
                                \                         /
                                 \                       /
            X----------X----------X                     X----------X----------X
             \                   / \                   / \                   /
              \                 /   \                 /   \                 /
               \               /     \               /     \               /
                \    LEFT     /       \             /       \    RIGHT    /
       leftInner X           X         \           /         X           X rightInner
                  \         /           \         /           \         /
                   \       /             \       /             \       /
                    \     /               \     /               \     /
                     \   /                 \   /                 \   /
                      \ /                   \ /                   \ /
                       X                     X                     X
                                           bottom

--------------------------------------------------------------------------------------------------*/



/**
  * Class for sierpinski calculation and drawing triangles to canvas
  * @param context
  * @param attrs
  */
class Sierpinski(var context: Context, val attrs: AttributeSet) extends View(context, attrs)
{

  val displayWidth = this.getResources.getDisplayMetrics.widthPixels
  val displayHeight = this.getResources.getDisplayMetrics.heightPixels
  var recursionDepth = 0


  def setRecursionDepth(depth : Int) : Unit = recursionDepth = depth


  override protected def onDraw(canvas: Canvas)
  {
    super.onDraw(canvas)

    //custom types for coordinates and triangle
    type Coordinate = (Float, Float)
    type Triangle = Map[String, Coordinate]


    /**
      * Draws the calculated Triangle to the canvas
      * @param triangle   Triangle to draw
      */
    def drawTriangle(triangle : Triangle) : Unit =
    {
      val path : Path = new Path()
      val paint: Paint = new Paint()
      paint.setStyle(Paint.Style.STROKE)

      val leftOuter : Coordinate = triangle.getOrElse("leftOuter", null)
      val rightOuter : Coordinate = triangle.getOrElse("rightOuter", null)
      val bottom : Coordinate = triangle.getOrElse("bottom", null)

      path.moveTo(leftOuter._1, leftOuter._2)
      path.lineTo(rightOuter._1, rightOuter._2)
      path.lineTo(bottom._1, bottom._2)
      path.lineTo(leftOuter._1, leftOuter._2)
      canvas.drawPath(path, paint)
    }



    /**
      * Calculates the base triangle depending on screensize of device
      * @return     Coordinates of the base triangle
      */
    def calcBaseTriangle(): Triangle =
    {
      val triangleSize = displayWidth / 2
      val triangleHeight = Math.sqrt(Math.pow(triangleSize, 2) - Math.pow(triangleSize/2, 2))

      //Draw outer triangle
      val path : Path = new Path()
      val paint: Paint = new Paint()
      paint.setStyle(Paint.Style.STROKE)
      path.moveTo(0, displayHeight  - displayHeight/ 4)
      path.lineTo(displayWidth, displayHeight  - displayHeight/ 4)
      path.lineTo(displayWidth / 2, (displayHeight  - displayHeight / 4 - triangleHeight * 2).asInstanceOf[Float])
      path.lineTo(0, displayHeight  - displayHeight/ 4)
      canvas.drawPath(path, paint)

      val bottom : Coordinate = (displayWidth / 2, displayHeight  - displayHeight/ 4)
      val top : Coordinate = (bottom._1, (bottom._2 - triangleHeight).asInstanceOf[Float])
      val leftOuter : Coordinate = (bottom._1 - triangleSize / 2, top._2)
      val rightOuter : Coordinate = (leftOuter._1 + triangleSize, leftOuter._2)
      val leftInner : Coordinate = (bottom._1 - triangleSize/4, (bottom._2 - triangleHeight/2).asInstanceOf[Float])
      val rightInner : Coordinate = (bottom._1 + triangleSize/4, leftInner._2)

      Map("leftOuter"->leftOuter, "leftInner"->leftInner, "top"->top,
          "bottom"->bottom, "rightInner"->rightInner, "rightOuter"->rightOuter)
    }



    /**
      * Calculates the coordinates of the 3 surrounding trinagles
      * @param originTriangle   the base triangle to calc the others
      * @return                 the left, top and right surrounding triangles
      */
    def calcSurroundingTriangles(originTriangle: Triangle): (Triangle, Triangle, Triangle) =
    {
      val newTriangleSize = (originTriangle.get("rightOuter").get._1 - originTriangle.get("leftOuter").get._1) / 2
      val newTriangleHeight = Math.sqrt(Math.pow(newTriangleSize, 2) - Math.pow(newTriangleSize/2, 2))

      //Coordinates for left Triangle
      val rightOuterLT: Coordinate = originTriangle.getOrElse("leftInner", null)
      val topLT : Coordinate = (originTriangle.get("leftOuter").get._1, rightOuterLT._2)
      val leftOuterLT : Coordinate = (topLT._1 - newTriangleSize / 2, topLT._2)
      val leftInnerLT : Coordinate = (topLT._1 - newTriangleSize / 4, (topLT._2 + newTriangleHeight / 2).asInstanceOf[Float])
      val bottomLT : Coordinate = (originTriangle.get("leftOuter").get._1, originTriangle.get("bottom").get._2)
      val rightInnerLT : Coordinate = (topLT._1 + newTriangleSize / 4, (topLT._2 + newTriangleHeight / 2).asInstanceOf[Float])

      //Coordinates for top triangle
      val bottomTT : Coordinate = originTriangle.getOrElse("top", null)
      val leftInnerTT : Coordinate = (bottomTT._1 - newTriangleSize / 4, (bottomTT._2 - newTriangleHeight / 2).asInstanceOf[Float])
      val leftOuterTT : Coordinate = (originTriangle.get("leftInner").get._1, (bottomTT._2 - newTriangleHeight).asInstanceOf[Float])
      val topTT : Coordinate = (bottomTT._1, leftOuterTT._2)
      val rightOuterTT : Coordinate = (originTriangle.get("rightInner").get._1, topTT._2)
      val rightInnerTT : Coordinate = (topTT._1 + newTriangleSize / 4, leftInnerTT._2)

      //Coordinates for right Triangle
      val leftOuterRT : Coordinate = originTriangle.getOrElse("rightInner", null)
      val topRT : Coordinate = (originTriangle.get("rightOuter").get._1, leftOuterRT._2)
      val rightOuterRT : Coordinate = (topRT._1 + newTriangleSize / 2, topRT._2)
      val rightInnerRT : Coordinate = (topRT._1 + newTriangleSize / 4, (topRT._2 + newTriangleHeight / 2).asInstanceOf[Float])
      val bottomRT : Coordinate = (topRT._1, originTriangle.get("bottom").get._2)
      val leftInnerRT : Coordinate = (leftOuterRT._1 + newTriangleSize / 4, rightInnerRT._2)

      val leftTriangle = Map("leftOuter"->leftOuterLT, "leftInner"->leftInnerLT, "top"->topLT,
                              "bottom"->bottomLT, "rightInner"->rightInnerLT, "rightOuter"->rightOuterLT)
      val topTriangle = Map("leftOuter"->leftOuterTT, "leftInner"->leftInnerTT, "top"->topTT,
                              "bottom"->bottomTT, "rightInner"->rightInnerTT, "rightOuter"->rightOuterTT)
      val rightTriangle = Map("leftOuter"->leftOuterRT, "leftInner"->leftInnerRT, "top"->topRT,
                              "bottom"->bottomRT, "rightInner"->rightInnerRT, "rightOuter"->rightOuterRT)

      (leftTriangle, topTriangle, rightTriangle)
    }



    /**
      * Recursive solution for sierpinski triangles
      * @param triangle         the base triangle to calculate and draw the three surrounding triangles
      * @param recursionDepth   depth of recursion
      */
    def drawSierpinski(triangle: Triangle, recursionDepth: Int) : Unit =
    {
      val newTriangles = calcSurroundingTriangles(triangle)

      if (recursionDepth > 0)
      {
        drawTriangle(newTriangles._1)
        drawTriangle(newTriangles._2)
        drawTriangle(newTriangles._3)

        drawSierpinski(newTriangles._1, recursionDepth - 1)
        drawSierpinski(newTriangles._2, recursionDepth - 1)
        drawSierpinski(newTriangles._3, recursionDepth - 1)
      }
    }


    //at first calc and draw the base triangle (biggest one)
    val baseTriangle : Triangle = calcBaseTriangle()
    drawTriangle(baseTriangle)

    //draw sierpinski triangles with given recursion depth
    drawSierpinski(baseTriangle, recursionDepth)
  }
}
