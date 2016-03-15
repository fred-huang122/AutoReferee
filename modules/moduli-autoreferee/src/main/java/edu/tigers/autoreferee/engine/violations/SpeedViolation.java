/*
 * *********************************************************
 * Copyright (c) 2009 - 2016, DHBW Mannheim - Tigers Mannheim
 * Project: TIGERS - Sumatra
 * Date: Feb 27, 2016
 * Author(s): "Lukas Magel"
 * *********************************************************
 */
package edu.tigers.autoreferee.engine.violations;

import java.text.DecimalFormat;

import edu.tigers.autoreferee.engine.FollowUpAction;
import edu.tigers.sumatra.ids.BotID;


/**
 * @author "Lukas Magel"
 */
public class SpeedViolation extends RuleViolation
{
	/** speed in m/s */
	private final double	speed;
	
	
	/**
	 * @param violationType
	 * @param timestamp
	 * @param botAtFault
	 * @param followUp
	 * @param speed in m/s
	 */
	public SpeedViolation(final ERuleViolation violationType, final long timestamp, final BotID botAtFault,
			final FollowUpAction followUp, final double speed)
	{
		super(violationType, timestamp, botAtFault, followUp);
		this.speed = speed;
	}
	
	
	/**
	 * The speed value which lead to this violation
	 * 
	 * @return the speed in m/s
	 */
	public double getSpeed()
	{
		return speed;
	}
	
	
	@Override
	public String buildLogString()
	{
		return super.buildLogString();
	}
	
	
	@Override
	protected String generateLogString()
	{
		DecimalFormat format = new DecimalFormat("#.000");
		
		String superResult = super.generateLogString();
		StringBuilder builder = new StringBuilder(superResult);
		
		builder.append(" | Speed: ");
		builder.append(format.format(speed));
		builder.append("m/s");
		
		return builder.toString();
	}
}