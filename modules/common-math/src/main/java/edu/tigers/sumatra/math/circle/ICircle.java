/*
 * Copyright (c) 2009 - 2018, DHBW Mannheim - TIGERs Mannheim
 */

package edu.tigers.sumatra.math.circle;

import java.util.List;

import edu.tigers.sumatra.math.ellipse.IEllipse;
import edu.tigers.sumatra.math.line.ILine;
import edu.tigers.sumatra.math.line.v2.ILineSegment;
import edu.tigers.sumatra.math.vector.IVector2;
import edu.tigers.sumatra.math.vector.IVector3;


/**
 * Circle interface.
 * 
 * @author Malte
 */
public interface ICircle extends ICircular
{
	/**
	 * Get the intersection points of the two tangential lines that cross the external points.
	 *
	 * @param externalPoint some point
	 * @return two tangential intersections
	 */
	List<IVector2> tangentialIntersections(final IVector2 externalPoint);
	
	
	/**
	 * Project <i>this</i> to ground assuming it is at <i>height</i> height and the projection origin is <i>origin</i>.
	 * 
	 * @param origin Projection origin
	 * @param height Height of the circle before projection.
	 * @return Projected circle => ellipse
	 */
	IEllipse projectToGround(final IVector3 origin, final double height);
	
	
	/**
	 * Get the intersection points of the shape and segment
	 *
	 * @param line some line segment
	 * @return all intersection points
	 */
	List<IVector2> lineSegmentIntersections(ILine line);
	
	
	/**
	 * Get the intersection points of the shape and segment
	 *
	 * @param line some line segment
	 * @return all intersection points
	 */
	List<IVector2> lineSegmentIntersections(ILineSegment line);
	
	
	/**
	 * @param line the line segment
	 * @return true, if the line intersects the shape and intersection point is on segment
	 */
	default boolean isIntersectingWithLineSegment(ILine line)
	{
		return !lineSegmentIntersections(line).isEmpty();
	}
}
