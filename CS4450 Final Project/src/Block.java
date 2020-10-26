
public class Block {
    private boolean isActive;
    private BlockType type;
    private float x,y,z;
    
    public enum BlockType {
        Grass(0),
        Sand(1),
        Water(2),
        Dirt(3),
        Stone(4),
        Bedrock(5);
        
        private int blockID;
        
        BlockType(int i){
            blockID = i;
        }
        
        public int getID(){
            return blockID;
        }
        
        public void setID(int i){
            blockID = i;
        }
    }
    
    public Block(BlockType type){
        this.type = type;
    }
    
    public void setCoordinates(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public boolean getActive(){
        return isActive;
    }
    
    public void setActive(boolean active){
        isActive = active;
    }
    
    public int getID(){
        return type.getID();
    }
    
}
