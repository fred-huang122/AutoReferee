/*
 * Copyright (c) 2009 - 2018, DHBW Mannheim - TIGERs Mannheim
 */

package edu.tigers.sumatra.drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.sleepycat.persist.model.Persistent;


/**
 * @author Nicolai Ommer <nicolai.ommer@gmail.com>
 */
@Persistent
public class ShapeMap
{
	private final Map<String, ShapeLayer> categories;
	
	private static boolean persistDebugShapes = true;
	
	
	/**
	 * Create new empty shape map
	 */
	public ShapeMap()
	{
		this(new HashMap<>());
	}
	
	
	/**
	 * Deep copy (shapes not copied)
	 * 
	 * @param o original map
	 */
	public ShapeMap(final ShapeMap o)
	{
		this(new HashMap<>());
		for (ShapeLayer sl : o.categories.values())
		{
			categories.put(sl.identifier.getId(), new ShapeLayer(sl));
		}
	}
	
	
	private ShapeMap(Map<String, ShapeLayer> map)
	{
		categories = map;
	}
	
	
	public static ShapeMap unmodifiableCopy(ShapeMap s)
	{
		Map<String, ShapeLayer> categories = new HashMap<>();
		for (ShapeLayer sl : s.categories.values())
		{
			categories.put(sl.identifier.getId(),
					new ShapeLayer(sl.identifier, Collections.unmodifiableList(sl.shapes), sl.inverted));
		}
		return new ShapeMap(Collections.unmodifiableMap(categories));
	}
	
	
	/**
	 * @param persistDebugShapes should debug shapes be persisted?
	 */
	public static void setPersistDebugShapes(final boolean persistDebugShapes)
	{
		ShapeMap.persistDebugShapes = persistDebugShapes;
	}
	
	
	/**
	 * Get list for layer and category
	 *
	 * @param identifier
	 * @return
	 */
	public List<IDrawableShape> get(final IShapeLayer identifier)
	{
		return categories.computeIfAbsent(identifier.getId(), k -> new ShapeLayer(identifier)).shapes;
	}
	
	
	/**
	 * Remove all shapes that should not be persisted
	 */
	public void removeNonPersistent()
	{
		categories.entrySet().removeIf(en -> !persist(en.getValue().identifier));
	}
	
	
	private boolean persist(IShapeLayer identifier)
	{
		return identifier.getPersistenceType() == EShapeLayerPersistenceType.ALWAYS_PERSIST ||
				(persistDebugShapes && identifier.getPersistenceType() == EShapeLayerPersistenceType.DEBUG_PERSIST);
	}
	
	
	/**
	 * @return
	 */
	public List<IShapeLayer> getAllShapeLayersIdentifiers()
	{
		return categories.values().stream().sorted().map(sl -> sl.identifier)
				.collect(Collectors.toList());
	}
	
	
	/**
	 * @return
	 */
	public List<ShapeLayer> getAllShapeLayers()
	{
		return categories.values().stream().sorted()
				.collect(Collectors.toList());
	}
	
	
	/**
	 * @param inverted
	 */
	public void setInverted(final boolean inverted)
	{
		categories.values().forEach(sl -> sl.inverted = inverted);
	}
	
	
	@Override
	public String toString()
	{
		return new ToStringBuilder(this)
				.append("categories", categories)
				.toString();
	}
	
	/**
	 * Type of persistence for shape layers
	 */
	public enum EShapeLayerPersistenceType
	{
		ALWAYS_PERSIST,
		NEVER_PERSIST,
		DEBUG_PERSIST
	}
	
	@Persistent
	public static class ShapeLayer implements Comparable<ShapeLayer>
	{
		final IShapeLayer identifier;
		final List<IDrawableShape> shapes;
		boolean inverted = false;
		
		
		@SuppressWarnings("unused")
		private ShapeLayer()
		{
			identifier = null;
			shapes = Collections.emptyList();
		}
		
		
		/**
		 * @param identifier
		 */
		public ShapeLayer(final IShapeLayer identifier)
		{
			this.identifier = identifier;
			shapes = new ArrayList<>();
		}
		
		
		public ShapeLayer(final IShapeLayer identifier, final List<IDrawableShape> shapes, final boolean inverted)
		{
			this.identifier = identifier;
			this.shapes = shapes;
			this.inverted = inverted;
		}
		
		
		/**
		 * @param o
		 */
		public ShapeLayer(final ShapeLayer o)
		{
			identifier = o.identifier;
			shapes = new ArrayList<>(o.shapes);
			inverted = o.inverted;
		}
		
		
		public IShapeLayer getIdentifier()
		{
			return identifier;
		}
		
		
		public List<IDrawableShape> getShapes()
		{
			return shapes;
		}
		
		
		public boolean isInverted()
		{
			return inverted;
		}
		
		
		@Override
		public String toString()
		{
			return new ToStringBuilder(this)
					.append("identifier", identifier)
					.append("shapes", shapes)
					.append("inverted", inverted)
					.toString();
		}
		
		
		@Override
		public int compareTo(final ShapeLayer o)
		{
			return Integer.compare(identifier.getOrderId(), o.identifier.getOrderId());
		}
		
		
		@Override
		public boolean equals(final Object o)
		{
			if (this == o)
				return true;
			
			if (o == null || getClass() != o.getClass())
				return false;
			
			final ShapeLayer that = (ShapeLayer) o;
			
			return new EqualsBuilder()
					.append(identifier, that.identifier)
					.isEquals();
		}
		
		
		@Override
		public int hashCode()
		{
			return new HashCodeBuilder(17, 37)
					.append(identifier)
					.toHashCode();
		}
	}
}