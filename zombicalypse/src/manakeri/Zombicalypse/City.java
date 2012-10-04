package manakeri.Zombicalypse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g3d.model.still.StillSubMesh;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class City {
	public static int getnumVertices() {
		return _numVertices;
	}

	private int[][] grid;
	private final int _width, _height;
	private Mesh houses;
	private final Texture tex;
	private final SpriteCache scache;
	private final int cacheID;
	private static int _numVertices;

	private int road_tiles;
	private boolean _initialized = false;

	public City(int width, int height) {
		if (TGame.DEBUG) {
			Gdx.app.log(TGame.TAG, "city.constructor");
		}

		_width = width;
		_height = height;

		if (TGame.DEBUG) {
			Gdx.app.log(TGame.TAG, "city.build()");
		}
		build();

		Sprite[] sprite = new Sprite[3];
		sprite[0] = TGame.atlas.createSprite("road1");
		sprite[1] = TGame.atlas.createSprite("road2");
		sprite[2] = TGame.atlas.createSprite("road3");

		if (TGame.DEBUG) {
			Gdx.app.log(TGame.TAG, "city.scache");
		}
		scache = new SpriteCache(width * height, true);
		scache.beginCache();
		for (int x = 0; x < _width; x++) {
			for (int y = 0; y < _height; y++) {
				int tile = grid[x][y];
				if (tile >= 128) {
					Sprite sp = sprite[tile - 128];
					sp.setBounds(x, y, 1, 1);
					scache.add(sp);
				}
			}
		}
		cacheID = scache.endCache();
		Matrix4 matrix = new Matrix4();
		matrix.setToRotation(new Vector3(1, 0, 0), 90);
		scache.setTransformMatrix(matrix);

		if (TGame.DEBUG) {
			Gdx.app.log(TGame.TAG, "city.generateVA()");
		}
		generateVA();
		tex = new Texture("data/img/house.png");

		_initialized = true;
	}

	private void build() {
		road_tiles = 0;
		grid = new int[_width][_height];
		for (int x = 0; x < _width; x++) {
			for (int y = 0; y < _height; y++) {
				if (x == 0 || y == 0 || x == _width - 1 || y == _height - 1) {
					grid[x][y] = TGame.rand.nextInt(3);
				} else if ((x % 4 == 0) && (y % 3 == 0)) { // crossroad
					grid[x][y] = 128;
					road_tiles++;
				} else if (x % 4 == 0) { // road x
					grid[x][y] = 128 + 1;
					road_tiles++;
				} else if (y % 3 == 0) { // road y
					grid[x][y] = 128 + 2;
					road_tiles++;
				} else {
					grid[x][y] = TGame.rand.nextInt(TGame.house.length);
				}

			}
		}
	}

	public void dispose() {
		scache.dispose();
		tex.dispose();
	}

	public void draw(Camera cam) {
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);

		tex.bind();
		TGame.texshader.begin();
		TGame.texshader.setUniformi("u_texture", 0);
		TGame.texshader.setUniformMatrix("u_worldView", cam.combined);
		houses.render(TGame.texshader, GL20.GL_TRIANGLES);
		TGame.texshader.end();

		scache.setProjectionMatrix(cam.combined);
		scache.begin();
		scache.draw(cacheID);
		scache.end();

		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
	}

	private void generateVA() {
		int house_vertices = 0;
		for (int x = 0; x < _width; x++) {
			for (int y = 0; y < _height; y++) {
				if (grid[x][y] < 128) {
					for (StillSubMesh mesh : TGame.house[grid[x][y]].subMeshes) {
						house_vertices += mesh.mesh.getNumVertices();
					}
					// Gdx.app.log(TGame.TAG, "all houses vertices: " +
					// house_vertices);
				}
			}
		}

		houses = new Mesh(true, house_vertices * 8, 0,
				TGame.house[0].subMeshes[0].mesh.getVertexAttributes());
		float[] vertices = new float[house_vertices * 8];

		int vi = 0;
		for (int x = 0; x < _width; x++) {
			for (int z = 0; z < _height; z++) {
				if (grid[x][z] < 128) {
					float[] buf = new float[TGame.house[grid[x][z]].subMeshes[0].mesh
							.getNumVertices() * 8];
					TGame.house[grid[x][z]].subMeshes[0].mesh.getVertices(buf);
					for (int i = 0; i < buf.length;) {
						vertices[vi++] = buf[i++] + x;
						vertices[vi++] = buf[i++] + 0;
						vertices[vi++] = buf[i++] + z + 1;

						vertices[vi++] = buf[i++];
						vertices[vi++] = buf[i++];
						vertices[vi++] = buf[i++];

						vertices[vi++] = buf[i++];
						vertices[vi++] = buf[i++];
					}
				}
			}
		}
		houses.setVertices(vertices);

		_numVertices = house_vertices + road_tiles * 6;
	}

	public int getHeight() {
		return _height;
	}

	/**
	 * Returns the type of the tile at given position
	 * 
	 * @param x
	 *            X position of the tile at range 0..1 as float
	 * @param z
	 *            Z position of the tile at range 0..1 as float
	 * @return Tile type
	 */
	public int getTile(float x, float z) {
		return getTile((int) (x * _width), (int) (z * _height));
	}

	/**
	 * Returns the type of the tile at given position
	 * 
	 * @param x
	 *            X position of the tile
	 * @param z
	 *            Z position of the tile
	 * @return Tile type
	 */
	public int getTile(int x, int z) {
		if (x >= _width) {
			return -1;
		}
		if (z >= _height) {
			return -1;
		}

		return grid[x][z];
	}

	public int getWidth() {
		return _width;
	}

	public boolean isInitialized() {
		return _initialized;
	}

	/**
	 * Is the tile road or not
	 * 
	 * @param x
	 *            X position of the tile at range 0..1 as float
	 * @param z
	 *            Z position of the tile at range 0..1 as float
	 */
	public boolean isRoad(float x, float z) {
		return isRoad((int) (x * _width), (int) (z * _height));
	}

	/**
	 * Is the tile road or not
	 * 
	 * @param x
	 *            X position of the tile
	 * @param z
	 *            Z position of the tile
	 */
	public boolean isRoad(int x, int z) {
		if (getTile(x, z) > 127) {
			return true;
		}
		return false;
	}

}
