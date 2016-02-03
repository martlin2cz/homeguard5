package cz.martlin.hg5.ws.services;

import java.awt.Color;
import java.util.Calendar;
import java.util.Map;

import cz.martlin.hg5.logic.data.SoundTrack;
import cz.martlin.hg5.web.HomeguardSingleton;
import cz.martlin.hg5.web.charts.RIsimpleChartRenderer;
import cz.martlin.hg5.ws.WebServiceProcessor;
import cz.martlin.hg5.ws.WebServiceUtils;

/**
 * Calls renderer for track's simple chart. Requires params at (or at-unix),
 * width, height, and optionally mins, avgs, maxs, background and useCache
 * (false on unspecified) .
 * 
 * @author martin
 *
 */
public class ReportItemChartRenderer implements WebServiceProcessor {
	private static final String IMAGE_FORMAT = RIsimpleChartRenderer.FORMAT;
	private static final String MIME = "image/" + IMAGE_FORMAT;

	private final RIsimpleChartRenderer renderer = new RIsimpleChartRenderer();

	public ReportItemChartRenderer() {
	}

	@Override
	public String getContentType() {
		return MIME;
	}

	@Override
	public byte[] process(Map<String, String[]> request) throws Exception {
		Calendar recordedAt = WebServiceUtils.parseDateTime("at", "at-unix", request);
		int width = WebServiceUtils.parseNumber("width", request);
		int height = WebServiceUtils.parseNumber("height", request);
		Color mins = WebServiceUtils.parseColorOrDefault("mins", request, RIsimpleChartRenderer.MINS_DEFAULT_COLOR);
		Color avgs = WebServiceUtils.parseColorOrDefault("avgs", request, RIsimpleChartRenderer.AVGS_DEFAULT_COLOR);
		Color maxs = WebServiceUtils.parseColorOrDefault("maxs", request, RIsimpleChartRenderer.MAXS_DEFAULT_COLOR);
		Color bg = WebServiceUtils.parseColorOrDefault("background", request, RIsimpleChartRenderer.BG_DEFAULT_COLOR);
		boolean useCache = WebServiceUtils.parseBoolean("useCache", request, null, "false");

		SoundTrack track = HomeguardSingleton.get().getTrack(recordedAt);

		if (track == null) {
			throw new IllegalArgumentException("There is no such soundtrack at " + recordedAt.getTime());
		} else {
			if (useCache) {
				return renderer.getChartCached(track, width, height, mins, avgs, maxs, bg);
			} else {
				return renderer.getChart(track, width, height, mins, avgs, maxs, bg);
			}
		}
	}

}
