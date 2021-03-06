package gov.nasa.arc.dert.scenegraph;

import gov.nasa.arc.dert.landscape.Landscape;
import gov.nasa.arc.dert.landscape.QuadTree;

import java.nio.FloatBuffer;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.FloatBufferData;
import com.ardor3d.scenegraph.Line;

/**
 * A line object with multiple segments.
 *
 */
public class LineStrip extends Line {

	/**
	 * Constructor
	 * 
	 * @param name
	 * @param vertex
	 * @param normal
	 * @param color
	 * @param coords
	 */
	public LineStrip(final String name, final FloatBuffer vertex, final FloatBuffer normal, final FloatBuffer color,
		final FloatBufferData coords) {
		super(name, vertex, normal, color, coords);
		getMeshData().setIndexMode(IndexMode.LineStrip);
	}

	/**
	 * Update the elevation when the landscape changes.
	 * 
	 * @param quadTree
	 */
	public synchronized void updateElevation(QuadTree quadTree) {
		FloatBuffer vertex = _meshData.getVertexBuffer();
		int n = vertex.limit();
		Landscape landscape = Landscape.getInstance();
		for (int i = 0; i < n; i += 3) {
			float x = vertex.get(i);
			float y = vertex.get(i + 1);
			double z = landscape.getZ(x, y) + 0.1;
			if (!Double.isNaN(z)) {
				vertex.put(i + 2, (float) z);
			}
		}
	}

	/**
	 * Determine if this line strip intersects with a quad tree.
	 * 
	 * @param quadTree
	 * @return
	 */
	public boolean intersects(QuadTree quadTree) {
		Vector3[] testPoints = quadTree.getTestPoints();
		BoundingBox bbox = (BoundingBox) getWorldBound();
		// check for overlap or quadtree is completely inside linestrip bbox
		for (int i = 0; i < testPoints.length; ++i) {
			if (bbox.contains(testPoints[i])) {
				return (true);
			}
		}

		// check if is linestrip completely inside quadtree
		ReadOnlyVector3 center = new Vector3(bbox.getCenter());
		double x = center.getX() - bbox.getXExtent();
		double y = center.getY() - bbox.getYExtent();
		if (quadTree.contains(x, y)) {
			return (true);
		}
		x = center.getX() + bbox.getXExtent();
		y = center.getY() + bbox.getYExtent();
		if (quadTree.contains(x, y)) {
			return (true);
		}
		return (false);
	}

}
