[คลาสไดอะแกรม.txt](https://github.com/user-attachments/files/32678337/default.txt)
DrawShape{
+ ShapeType : Enum
- shape : Shape
- points : List<Point>
- test : String
- textPosition : Point
- color : Color
- StrokeWidth : float
- font : Font

///

+ DrawnShape(type : ShapeType, shape : Shape, color : Color, strokeWidth : float)
+ DrawnShape(type : ShapeType, points : List<Point>, color : Color, strokeWidth : float)
+ DrawnShape(text : String, pos : Point, font : Font, color : Color)
+ getType() : ShapeType
+ draw(g2d : Graphics2D) : void
+ contains(p : Point) : boolean 
}

Layer{
- name : String
- visible : Boolean
- shapes : List<DrawnShape>
- backgroundImage : BufferedImage

///

+ Layer(name : String)
+ getName : String
+ setName(name : String) : void
+ isVisible : Boolean
+ setVisible(visible : boolean) : void
+ getShapes() : List<DrawnShape>
+ addShape(shape : DrawnShape) : void
+ removeShape(shape : DrawnShape) : void
+ getBackgroundImage() : BufferedImage
+ setBackgroundImage(img : BufferedImage) : void
+ cloneLayer() : Layer
}


DrawingModel{
- layers : List<Layer>
- activeLayerIndex : int
- currentTool : DrawnShape.ShapeType
- currentColor : Color
- pencilSize : float
- eraserSize : float
- zoomScale : double
- globalVisibility : Boolean
- undoStack : Stack<List<Layer>>
- redoStack : Stack<List<Layer>>

///

+ public DrawingModel()
+ getLayers() : List<Layer>
+ getActiveLayer() : Layer
+ getActiveLayerIndex() : int
+ setActiveLayerIndex(index : int) : void
+ addLayer() : void
+ removeActiveLayer() : void
+ toggleGlobalVisibility() : void
+ getCurrentTool() : DrawnShape.ShapeType
+ setCurrentTool(tool : DrawnShape.ShapeType) : void
+ getCurrentColor() : Color
+ setCurrentColor(color : Color) : void
+ getPencilSize() : float
+ setPencilSize(size : float) : void
+ getEraserSize() : float
+ setEraserSize(size : float) : void
+ getZoomScale() : double
+ setZoomScale(zoom : double) : void
+ saveStateForUndo() : void
+ undo() : void
+ redo() : void
+ cloneLayers(original : List<Layer>) : List<Layer>
}


PaintPanel{
- model : DrawingModel model
- startPoint : Point
- currentPoint : Point
- freehandPoints : List<Point>
- BASE_WIDTH : int
- BASE_HEIGHT : int

///

+ PaintPanel(model : DrawingModel)
- scalePoint(p : Point) : Point
- makeRectangle(p1 : Point, p2 : Point) : Rectangle2D.Float
- makeEllipse(p1 : Point, p2 : Point) : Ellipse2D.Float
- isInsideCanvas(p : Point) : Boolean
- clampPoint(p : Point) : Point
+ updateCanvasSize() : void
# paintComponent(g : Graphics) : void
- drawLayerAndPreview(g2d : Graphics2D) : void
}


ToolsPanel{
- model : DrawingModel
- colorPreviewBtn : JButton

///

+ ToolsPanel(model : DrawingModel)
}


Layers{
- model : DrawingModel
- listModel : DefaultListModel<String>
- layerJList : JList<String>
- toggleAllBtn : JButton

///

+ Layers(model : DrawingModel, paintPanel : PaintPanel)
- moveLayer(direction : int) : void
+ updateLayerList() : void
}



MenuPanel{
- model : DrawingModel
- paintPanel : PaintPanel
- currentSaveFile : File

///

+ MenuPanel(model : DrawingModel, paintPanel : PaintPanel, layers : Layers)
- setupShortcuts(undoAction : Action, redoAction : Action) : void
- openPNGImage() : void
- savePNGImage(saveAs : boolean) : void
}


ZoomPanel{
- model : DrawingModel
- paintPanel : PaintPanel
- zoomLabel : JLabel

///

+ ZoomPanel(model : DrawingModel, paintPanel : PaintPanel)
}



Gui{
- model : DrawingModel
- toolsPanel : ToolaPanel
- menuPanel : MenuPanel
- paintPanel : PaintPanel
- zoomPanel : ZoomPanel
- layersPanel : Layers

///

+ Gui()
+ main(args : String[]) : void // เป็น static ต้องมีขีดเส้นใต้
}





