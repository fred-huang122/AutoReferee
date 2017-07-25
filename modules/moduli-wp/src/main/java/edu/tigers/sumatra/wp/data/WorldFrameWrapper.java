/*
 * Copyright (c) 2009 - 2017, DHBW Mannheim - TIGERs Mannheim
 */

package edu.tigers.sumatra.wp.data;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sleepycat.persist.model.Persistent;

import edu.tigers.sumatra.drawable.ShapeMap;
import edu.tigers.sumatra.ids.BotID;
import edu.tigers.sumatra.ids.EAiTeam;
import edu.tigers.sumatra.referee.data.GameState;
import edu.tigers.sumatra.referee.data.RefereeMsg;


/**
 * Wrapper for different worldframes
 * 
 * @author Nicolai Ommer <nicolai.ommer@gmail.com>
 */
@Persistent
public class WorldFrameWrapper
{
	private final SimpleWorldFrame simpleWorldFrame;
	private final RefereeMsg refereeMsg;
	private final ShapeMap shapeMap = new ShapeMap();
    private Set<BotID>                                        botsToInterchange = new HashSet<>();
	private GameState gameState = GameState.HALT;
	
	private final transient Map<EAiTeam, WorldFrame> worldFrames = new EnumMap<>(EAiTeam.class);
	
	
	@SuppressWarnings("unused")
	private WorldFrameWrapper()
	{
		simpleWorldFrame = null;
		refereeMsg = new RefereeMsg();
	}
	
	
	/**
	 * @param swf
	 * @param refereeMsg
	 * @param gameState
	 * @param botsToInterchange
	 */
	public WorldFrameWrapper(final SimpleWorldFrame swf, final RefereeMsg refereeMsg,
			final GameState gameState, final Set<BotID> botsToInterchange)
	{
		assert refereeMsg != null;
		assert swf != null;
		simpleWorldFrame = swf;
		this.refereeMsg = refereeMsg;
		this.gameState = gameState;
		this.botsToInterchange = botsToInterchange;
	}
	
	
	/**
	 * @param wfw instance to copy
	 */
	public WorldFrameWrapper(final WorldFrameWrapper wfw)
	{
		simpleWorldFrame = wfw.simpleWorldFrame;
		refereeMsg = wfw.refereeMsg;
		shapeMap.merge(wfw.shapeMap);
		worldFrames.putAll(wfw.worldFrames);
		gameState = wfw.gameState;
		botsToInterchange = wfw.botsToInterchange;
	}
	
	
	/**
	 * Create WF from swf
	 * 
	 * @param swf
	 * @param aiTeam
	 * @return
	 */
	private WorldFrame createWorldFrame(final SimpleWorldFrame swf, final EAiTeam aiTeam)
	{
		final WorldFrame wf;
		if (refereeMsg.getLeftTeam() == aiTeam.getTeamColor())
		{
			wf = new WorldFrame(swf, botsToInterchange, aiTeam, false);
		} else
		{
			// right team will be mirrored
			wf = new WorldFrame(swf.mirrored(), botsToInterchange, aiTeam, true);
		}
		return wf;
	}
	
	
	/**
	 * @return the simpleWorldFrame
	 */
	public SimpleWorldFrame getSimpleWorldFrame()
	{
		return simpleWorldFrame;
	}
	
	
	/**
	 * @param aiTeam
	 * @return the worldFrames
	 */
	public synchronized WorldFrame getWorldFrame(final EAiTeam aiTeam)
	{
		return worldFrames.computeIfAbsent(aiTeam, t -> createWorldFrame(simpleWorldFrame, t));
	}
	
	
	/**
	 * @return the refereeMsg
	 */
	public final RefereeMsg getRefereeMsg()
	{
		return refereeMsg;
	}
	
	
	/**
	 * @return the gameState
	 */
	public final GameState getGameState()
	{
		return gameState;
	}
	
	
	/**
	 * @return the shapeMap
	 */
	public final ShapeMap getShapeMap()
	{
		return shapeMap;
	}


    /**
     * @return the bots to interchange
     */
	public final Set<BotID> getBotsToInterchange() {
	    return botsToInterchange;
    }
}
