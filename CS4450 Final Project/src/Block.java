/***************************************************************
* file: Block.java
* author: N. Vinjamury, D. Edwards, L. Dall
* class: CS 4450 - Computer Graphics
*
* assignment: Semester Project - Final
* date last modified: 11/30/2020
*
* purpose: This program defines a block class with a type which
* consists of an ID, a location based on coordinates, and a boolean
* which defines if the block is active (visible)
****************************************************************/ 
public class Block {
    private boolean isActive;
    private BlockType type;
    private float x,y,z;
    
    /***************************************************************
    * enum: BlockType
    * purpose: defines block types to make them easier to be called
    * later
    ****************************************************************/
    public enum BlockType {
        Grass(0),
        Sand(1),
        Water(2),
        Dirt(3),
        Stone(4),
        Bedrock(5);
        
        private int blockID;
        
        /***************************************************************
        * method: BlockType
        * purpose: constructor for block types to refer to them by ids
        * instead of full names
        ****************************************************************/
        BlockType(int i){
            blockID = i;
        }
        
        /***************************************************************
        * method: getID
        * purpose: getter for the ID
        ****************************************************************/
        public int getID(){
            return blockID;
        }
        
        /***************************************************************
        * method: setID
        * purpose: setter for the ID
        ****************************************************************/
        public void setID(int i){
            blockID = i;
        }
    }
    
    /***************************************************************
    * method: Block
    * purpose: constructor for the block that initializes type as well
    ****************************************************************/
    public Block(BlockType type){
        this.type = type;
    }
    
    /***************************************************************
    * method: setCoordinates
    * purpose: setter for the coordinates of the block
    ****************************************************************/
    public void setCoordinates(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /***************************************************************
    * method: getActive
    * purpose: getter for if the block is visible or not
    ****************************************************************/
    public boolean getActive(){
        return isActive;
    }
    
    /***************************************************************
    * method: setActive
    * purpose: setter for if the block is visible or not
    ****************************************************************/
    public void setActive(boolean active){
        isActive = active;
    }
    
    /***************************************************************
    * method: getID
    * purpose: getter for the block ID
    ****************************************************************/
    public int getID(){
        return type.getID();
    }

    /***************************************************************
    * method: getType
    * purpose: getter for the block type
    ****************************************************************/
    public BlockType getType() {
            return type;
    }
    
}
