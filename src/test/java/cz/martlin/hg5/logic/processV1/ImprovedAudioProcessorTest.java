package cz.martlin.hg5.logic.processV1;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import cz.martlin.hg5.logic.config.Configuration;
import cz.martlin.hg5.logic.data.ReportItem;
import cz.martlin.hg5.logic.data.SoundTrack;
import cz.martlin.hg5.logic.processV1.fsman.FileSystemManTools;
import cz.martlin.hg5.web.charts.RIsimpleChartRenderer;

public class ImprovedAudioProcessorTest {

	private final Configuration config = new Configuration();
	private final ImprovedAudioProcessor processor = new ImprovedAudioProcessor(config);
	private final RIsimpleChartRenderer charts = new RIsimpleChartRenderer();
	private final FileSystemManTools fsmant = new FileSystemManTools(config);

	@Before
	public void before() {
		System.out.println("Using config: " + config);
	}

	@Test
	public void toSigned() {
		byte unss[] = new byte[] { 0x0, 0x01, (byte) 0xFF, (byte) 0x7F, (byte) 0x80 };

		byte sss[] = processor.toSigned(unss);

		assertEquals(-128, sss[0]);
		assertEquals(-127, sss[1]);
		assertEquals(127, sss[2]);
		assertEquals(-1, sss[3]);
		assertEquals(0, sss[4]);
	}

	@Test
	public void compareSimpleAverages() {
		double humNoise = calculateAverageIn("hum");
		System.out.println("Hum   average: " + humNoise);

		double someNoise = calculateAverageIn("some");
		System.out.println("Some average:  " + someNoise);

		double musicNoise = calculateAverageIn("music");
		System.out.println("Music average: " + musicNoise);

		assertTrue(humNoise < someNoise);
		assertTrue(someNoise < musicNoise);
		assertTrue(humNoise < musicNoise);
	}

	@Test
	public void calculateCharts() {
		File humChart = calculateChart("hum");
		System.out.println("Hum   chart: " + humChart);

		File someChart = calculateChart("some");
		System.out.println("Some chart:  " + someChart);

		File musicChart = calculateChart("music");
		System.out.println("Music chart: " + musicChart);
	}

	@Test
	public void compareReportsItems() {
		ReportItem humRI = calculateReportItem("hum");
		System.out.println("Hum   report item: " + humRI + "\n");

		ReportItem someRI = calculateReportItem("some");
		System.out.println("Some report item:  " + someRI + "\n");

		ReportItem musicRI = calculateReportItem("music");
		System.out.println("Music report item: " + musicRI + "\n");

		isLeast(humRI, someRI);
		isLeast(someRI, musicRI);
		isLeast(humRI, musicRI);

		// warinig: may depend on config. So, this tests the default
		// configuration as well
		assertFalse(humRI.isWarning());
		assertFalse(humRI.isCritical());

		assertFalse(humRI.isCritical());

		assertTrue(musicRI.isCritical());
		assertTrue(musicRI.isCritical());
	}

	private void isLeast(ReportItem leastRI, ReportItem mostRI) {
		assertTrue(leastRI.getWarningSamplesCount() < mostRI.getWarningSamplesCount());
		assertTrue(leastRI.getCriticalSamplesCount() < mostRI.getCriticalSamplesCount());

	}

	private ReportItem calculateReportItem(String name) {
		SoundTrack track = loadTrackOfName(name);
		Calendar when = Calendar.getInstance();
		return processor.analyzeSample(when, track);
	}

	private File calculateChart(String name) {
		final int width = 500;
		final int height = 100;

		SoundTrack track = loadTrackOfName(name);
		byte[] chart = charts.getChart(track, width, height, //
				RIsimpleChartRenderer.MINS_DEFAULT_COLOR, //
				RIsimpleChartRenderer.AVGS_DEFAULT_COLOR, //
				RIsimpleChartRenderer.MAXS_DEFAULT_COLOR, //
				RIsimpleChartRenderer.BG_DEFAULT_COLOR);

		return saveChart(name, chart);
	}

	private File saveChart(String name, byte[] chart) {
		OutputStream fous = null;
		try {
			File file = File.createTempFile(//
					"chart-of-" + name + "-", //
					"." + RIsimpleChartRenderer.FORMAT);
			fous = new FileOutputStream(file);
			IOUtils.write(chart, fous);
			return file;
		} catch (Exception e) {
		} finally {
			IOUtils.closeQuietly(fous);
		}
		return null;
	}

	private double calculateAverageIn(String name) {
		SoundTrack track = loadTrackOfName(name);
		double[] doubles = processor.samplesOfTrack(track);
		return average(doubles);
	}

	private SoundTrack loadTrackOfName(String name) {
		File file = getFileOf(name);
		return fsmant.loadSoundTrack(file);
	}

	private File getFileOf(String name) {
		String path = "test-samples/" + name + ".wav";
		URL url = getClass().getClassLoader().getResource(path);
		return new File(url.getPath());
	}

	private double average(double[] doubles) {
		double sum = 0;

		for (double d : doubles) {
			sum += d;
		}

		return (sum / (double) doubles.length);

	}

}
