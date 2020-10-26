
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
    
}
