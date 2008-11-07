package
{
    import mx.controls.Text;

    public class DynamicText extends Text {
    
        public var dattrib:uint = 0;
        private var pointId:uint = 0;
        
        public function DynamicText() {
            super();
        }
        
        public function setAttribute(attrib:uint):void {
            this.dattrib = attrib;
        }
        
        public function getAttribute():uint {
            return this.dattrib;    
        }
        
        public function setPointId(pointId:uint):void {
            this.pointId = pointId;
        }
        
        public function getPointId():uint {
            return this.pointId;    
        }
    }
}
