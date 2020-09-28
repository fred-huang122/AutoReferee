/*
 * Copyright (c) 2009 - 2020, DHBW Mannheim - TIGERs Mannheim
 */

package edu.tigers.sumatra.wp.data;

import edu.tigers.sumatra.bot.RobotInfo;
import edu.tigers.sumatra.geometry.Geometry;
import edu.tigers.sumatra.ids.BotID;
import edu.tigers.sumatra.ids.EAiTeam;
import edu.tigers.sumatra.ids.ETeamColor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * This frame contains world info for a specific AI.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WorldFrame extends SimpleWorldFrame
{
	private final Map<BotID, ITrackedBot> opponentBots;
	private final Map<BotID, ITrackedBot> tigerBotsVisible;
	private final Map<BotID, ITrackedBot> tigerBotsAvailable;
	private final ETeamColor teamColor;
	private final boolean inverted;


	public WorldFrame(final SimpleWorldFrame simpleWorldFrame, final EAiTeam team, final boolean inverted)
	{
		super(simpleWorldFrame);
		this.teamColor = team.getTeamColor();
		this.inverted = inverted;

		opponentBots = computeOpponentBots(simpleWorldFrame, team);
		tigerBotsAvailable = computeAvailableTigers(simpleWorldFrame, team);
		tigerBotsVisible = computeTigersVisible(simpleWorldFrame, team);
	}


	/**
	 * Providing a <strong>shallow</strong> copy of original (Thus new collections are created, but filled with the same
	 * values
	 *
	 * @param original
	 */
	public WorldFrame(final WorldFrame original)
	{
		super(original);
		teamColor = original.getTeamColor();
		inverted = original.isInverted();
		opponentBots = original.getOpponentBots();
		tigerBotsAvailable = original.getTigerBotsAvailable();
		tigerBotsVisible = original.getTigerBotsVisible();
	}


	private Stream<ITrackedBot> createStreamOfAiTeam(final SimpleWorldFrame swf, EAiTeam aiTeam)
	{
		return swf.getBots().values().stream()
				.filter(bot -> aiTeam.getTeamColor() == bot.getBotId().getTeamColor());
	}


	private Map<BotID, ITrackedBot> computeAvailableTigers(final SimpleWorldFrame simpleWorldFrame,
			final EAiTeam aiTeam)
	{
		Map<BotID, ITrackedBot> availableTigers = createStreamOfAiTeam(simpleWorldFrame, aiTeam)
				.collect(Collectors.toMap(ITrackedBot::getBotId, Function.identity()));
		return Collections.unmodifiableMap(availableTigers);
	}


	private Map<BotID, ITrackedBot> computeOpponentBots(final SimpleWorldFrame simpleWorldFrame, final EAiTeam aiTeam)
	{

		Map<BotID, ITrackedBot> opponents = simpleWorldFrame.getBots().values().stream()
				.filter(bot -> bot.getBotId().getTeamColor() == aiTeam.getTeamColor().opposite())
				.filter(bot -> Geometry.getFieldWBorders().isPointInShape(bot.getPos()))
				.map(bot -> {
					RobotInfo info = RobotInfo.stub(bot.getBotId(), bot.getTimestamp());
					return TrackedBot.newCopyBuilder(bot)
							.withBotInfo(info)
							.withState(bot.getFilteredState().orElse(bot.getBotState()))
							.build();
				})
				.collect(Collectors.toMap(TrackedBot::getBotId, Function.identity()));
		return Collections.unmodifiableMap(opponents);
	}


	private Map<BotID, ITrackedBot> computeTigersVisible(final SimpleWorldFrame simpleWorldFrame, final EAiTeam aiTeam)
	{
		Map<BotID, ITrackedBot> visible = simpleWorldFrame.getBots().values().stream()
				.filter(bot -> aiTeam.matchesColor(bot.getTeamColor()))
				.collect(Collectors.toMap(ITrackedBot::getBotId, Function.identity()));
		return Collections.unmodifiableMap(visible);
	}


	/**
	 * Get {@link ITrackedBot}
	 *
	 * @param botId of the bot
	 * @return tiger {@link TrackedBot}
	 */
	public ITrackedBot getTiger(final BotID botId)
	{
		return getBot(botId);
	}


	/**
	 * Get opponent {@link ITrackedBot}
	 *
	 * @param botId of the bot
	 * @return opponent {@link TrackedBot}
	 */
	public ITrackedBot getOpponentBot(final BotID botId)
	{
		return getBot(botId);
	}
}
