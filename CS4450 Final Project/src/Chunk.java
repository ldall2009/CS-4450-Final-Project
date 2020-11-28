 /***************************************************************
  * file: Chunk.java
  * author: N. Vinjamury, D. Edwards, L. Dall
  * class: CS 4450 - Computer Graphics
  *
  * assignment: Semester Project - Checkpoint 3
  * date last modified: 11/14/2020
  *
  * purpose: This program generates chunks of blocks that can be rendered at once
  *   using textures and simplex noises along with blocks and coordinates to
  *   to create them
  ****************************************************************/
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
	public static final int CHUNK_SIZE = 30;
	public static final int CUBE_LENGTH = 2;
	private Block[][][] blocks;
	private int VBOVertexHandle;
	private int VBOColorHandle;
	private int startX, startY, startZ;
	private Random r;
	private int VBOTextureHandle;
	private Texture texture;
	private double[][] heights;
	//private int scale;
	
	/***************************************************************
	 * method: Chunk
	 * purpose: sets up constructor with textures and start coords
	 *
	 ****************************************************************/
	public Chunk(int startX, int startY, int startZ){
		try{
			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
		}
		catch(IOException e) {
			System.out.print("Error!");
		}
		
		r = new Random();
		
		SimplexNoise noise = new SimplexNoise(30, 0.1, r.nextInt());
		heights = new double[CHUNK_SIZE][CHUNK_SIZE];
		//scale = 1;
		for(int x = 0; x < CHUNK_SIZE; x++){
			for(int z = 0; z < CHUNK_SIZE; z++){
				heights[x][z] = (double)(15 + startY + (int)(40*noise.getNoise(x,z)) * CUBE_LENGTH);
			}
		}
		
		blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
		
		Block.BlockType[] bottomLayer = new Block.BlockType[]{
			Block.BlockType.Bedrock
		};
		Block.BlockType[] middleLayer = new Block.BlockType[]{
			Block.BlockType.Dirt,
			Block.BlockType.Stone,
		};
		Block.BlockType[] topLayer = new Block.BlockType[]{
			Block.BlockType.Grass,
			Block.BlockType.Sand,
			Block.BlockType.Water,
		};
		
		for(int x = 0; x < CHUNK_SIZE; x++){
			for(int z = 0; z < CHUNK_SIZE; z++){
				for(int y = 0; y < heights[x][z]; y++){
					Block.BlockType[] layerBlocks = middleLayer;
					if(y == 0)
						layerBlocks = bottomLayer;
					else if(y == heights[x][z] - 1)
						layerBlocks = topLayer;
					
					blocks[x][y][z] = new Block(layerBlocks[r.nextInt(layerBlocks.length)]);
					
				}
			}
		}
		
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
	}

	private boolean inChunk(int value) { 
		return value >= 0 && value < CHUNK_SIZE;
	}
	
	public Block getBlockAtPoint(Vector3f position) {
		Block result = null;
		
		int x = -(int)Math.floor((position.x - startX +0.5)/CUBE_LENGTH);
		int y = -(int)Math.floor((position.y - startY)/CUBE_LENGTH);
		int z = -(int)Math.floor((position.z - startZ)/CUBE_LENGTH);
		
		if(inChunk(x) && inChunk(y) && inChunk(z) ) {
			result = blocks[x][y][z];
		}

		return result;
	}
	
	/***************************************************************
	 * method: render
	 * purpose: sets up respective push and pop matrix to render chunks
	 *
	 ****************************************************************/
	public void render(){
		glPushMatrix();
		glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
		glVertexPointer(3, GL_FLOAT, 0, 0L);
		glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
		glColorPointer(3, GL_FLOAT, 0, 0L);
		glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
		glBindTexture(GL_TEXTURE_2D, 1);
		glTexCoordPointer(2,GL_FLOAT,0,0L);
		glDrawArrays(GL_QUADS, 0, CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE*24);
		glPopMatrix();
	}
	
	/***************************************************************
	 * method: rebuildMesh (not overloaded)
	 * purpose: calls the other rebuildMesh method
	 *
	 ****************************************************************/
	public void rebuildMesh() {
		rebuildMesh(startX,startY,startZ);
	}
	
	/***************************************************************
	 * method: rebuildMesh
	 * purpose: sets up build every time a respective chunk should refresh
	 *
	 ****************************************************************/
	public void rebuildMesh(float startX, float startY, float startZ){
		
		VBOColorHandle = glGenBuffers();
		VBOVertexHandle = glGenBuffers();
		VBOTextureHandle = glGenBuffers();
		
		FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
		FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
		FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
		
		for(int x = 0; x < CHUNK_SIZE; x++){
			for(int y = 0; y < CHUNK_SIZE; y++){
				for(int z = 0; z < CHUNK_SIZE; z++){
					if(blocks[x][y][z] != null) {
						VertexPositionData.put(createCube(
							(float)(startX + x * CUBE_LENGTH),
							(float)(startY + y * CUBE_LENGTH),
							(float)(startZ + z * CUBE_LENGTH)
						));
						VertexColorData.put(createCubeVertexCol(getCubeColor(blocks[x][y][z])));
						VertexTextureData.put(createTexCube((float) 0, (float) 0, blocks[x][y][z]));
					}
				}
			}
		}
		
		VertexColorData.flip();
		VertexPositionData.flip();
		VertexTextureData.flip();
		
		glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
		glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
		glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
		glBufferData(GL_ARRAY_BUFFER, VertexTextureData,GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	/***************************************************************
	 * method: createCubeVertexCol
	 * purpose: sets up cubeColors array and initializes its values
	 * then returns it
	 ****************************************************************/
	private float[] createCubeVertexCol(float[] CubeColorArray){
		float[] cubeColors = new float[CubeColorArray.length*4*6];
		for(int i=0; i<cubeColors.length; i++){
			cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
		}
		return cubeColors;
	}
	
	/***************************************************************
	 * method: createTexUV
	 * purpose: sets up uv texturing so that terrain.png is enough
	 * to have all textures
	 ****************************************************************/
	private static float[] createTexUV(int u, int v, int rot) {
		float offset = (1024f/16)/1024f;
		
		switch(rot) {
			case 0:
				return new float[] {
					offset*(u+1), offset*(v+1),
					offset*(u), offset*(v+1),
					offset*(u), offset*(v),
					offset*(u+1), offset*(v),
				};
			case 1:
				return new float[] {
					offset*(u), offset*(v+1),
					offset*(u), offset*(v),
					offset*(u+1), offset*(v),
					offset*(u+1), offset*(v+1),
				};
			case 2:
				return new float[] {
					offset*(u), offset*(v),
					offset*(u+1), offset*(v),
					offset*(u+1), offset*(v+1),
					offset*(u), offset*(v+1),
				};
			case 3:
				return new float[] {
					offset*(u+1), offset*(v),
					offset*(u+1), offset*(v+1),
					offset*(u), offset*(v+1),
					offset*(u), offset*(v),
				};
			default:
				return new float[]{};
		}
	}
	
	/***************************************************************
	 * method: createTexCube
	 * purpose: generates the coordinates of the textures to the cubes
	 *
	 ****************************************************************/
	public static float[] createTexCube(float x, float y, Block block) {
		final int VERTEX_COUNT = 4*2;
		
		//float offset = (1024f/16)/1024f;
		
		float[][] faces;
		
		switch (block.getType()) {
			case Dirt:
				faces = new float[][] {
					createTexUV(2, 0, 0), // Top
					createTexUV(2, 0, 0), // Bottom
					createTexUV(2, 0, 2), // Front
					createTexUV(2, 0, 0), // Back
					createTexUV(2, 0, 2), // Left
					createTexUV(2, 0, 0), // Right
				};
				break;
			case Grass:
				faces = new float[][] {
					createTexUV(2, 9, 0), // Top
					createTexUV(2, 0, 0), // Bottom
					createTexUV(3, 0, 2), // Front
					createTexUV(3, 0, 0), // Back
					createTexUV(3, 0, 2), // Left
					createTexUV(3, 0, 0), // Right
				};
				break;
			case Water:
				faces = new float[][] {
					createTexUV(1, 11, 0), // Top
					createTexUV(1, 11, 0), // Bottom
					createTexUV(1, 11, 2), // Front
					createTexUV(1, 11, 0), // Back
					createTexUV(1, 11, 2), // Left
					createTexUV(1, 11, 0), // Right
				};
				break;
			case Stone:
				faces = new float[][] {
					createTexUV(1, 0, 0), // Top
					createTexUV(1, 0, 0), // Bottom
					createTexUV(1, 0, 2), // Front
					createTexUV(1, 0, 0), // Back
					createTexUV(1, 0, 2), // Left
					createTexUV(1, 0, 0), // Right
				};
				break;
			case Bedrock:
				faces = new float[][] {
					createTexUV(1, 1, 0), // Top
					createTexUV(1, 1, 0), // Bottom
					createTexUV(1, 1, 2), // Front
					createTexUV(1, 1, 0), // Back
					createTexUV(1, 1, 2), // Left
					createTexUV(1, 1, 0), // Right
				};
				break;
			case Sand:
				faces = new float[][] {
					createTexUV(2, 1, 0), // Top
					createTexUV(2, 1, 0), // Bottom
					createTexUV(2, 1, 2), // Front
					createTexUV(2, 1, 0), // Back
					createTexUV(2, 1, 2), // Left
					createTexUV(2, 1, 0), // Right
				};
				break;
			default:
				faces = new float[][] {
					createTexUV(2, 9, 0), // Top
					createTexUV(2, 0, 0), // Bottom
					createTexUV(3, 0, 2), // Front
					createTexUV(3, 0, 0), // Back
					createTexUV(3, 0, 2), // Left
					createTexUV(3, 0, 0), // Right
				};
				break;
		}
		
		float[] texture = new float[faces.length*VERTEX_COUNT];
		
		for(int i=0; i<faces.length; ++i)
			for(int j=0; j<VERTEX_COUNT; ++j) {
				// Even face indices (0, 2) are for x
				// Odd face indices (1, 3) are for y
				if(j%2==0)
					texture[i*VERTEX_COUNT + j] = faces[i][j]+x;
				else
					texture[i*VERTEX_COUNT + j] = faces[i][j]+y;
			}
		
		return texture;
	}
	
	/***************************************************************
	 * method: createCube
	 * purpose: generates the coordinates of the quads to render for
	 * cube chunks
	 ****************************************************************/
	private static float[] createCube(float x, float y, float z){
		int offset = CUBE_LENGTH/2;
		return new float[] {
			//top quad
			x+offset, y+offset, z,
			x-offset, y+offset, z,
			x-offset, y+offset, z - CUBE_LENGTH,
			x+offset, y+offset, z - CUBE_LENGTH,
			//bottom quad
			x+offset, y-offset, z - CUBE_LENGTH,
			x-offset, y-offset, z - CUBE_LENGTH,
			x-offset, y-offset, z,
			x+offset, y-offset, z,
			//front quad
			x+offset, y+offset, z - CUBE_LENGTH,
			x-offset, y+offset, z - CUBE_LENGTH,
			x-offset, y-offset, z - CUBE_LENGTH,
			x+offset, y-offset, z - CUBE_LENGTH,
			//back quad
			x+offset, y-offset, z,
			x-offset, y-offset, z,
			x-offset, y+offset, z,
			x+offset, y+offset, z,
			//left quad
			x-offset, y+offset, z - CUBE_LENGTH,
			x-offset, y+offset, z,
			x-offset, y-offset, z,
			x-offset, y-offset, z - CUBE_LENGTH,
			//right quad
			x+offset, y+offset, z,
			x+offset, y+offset, z - CUBE_LENGTH,
			x+offset, y-offset, z - CUBE_LENGTH,
			x+offset, y-offset, z
		};
	}
	
	/***************************************************************
	 * method: getCubeColor
	 * purpose: sets up method to getCubeColor so that it is visible
	 *
	 ****************************************************************/
	private float[] getCubeColor(Block block){
		return new float[]{1,1,1};
	}
	
}
