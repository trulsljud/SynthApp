/**
 * 
 * @author Peter Brinkmann (peter.brinkmann@gmail.com)
 * 
 * For information on usage and redistribution, and for a DISCLAIMER OF ALL
 * WARRANTIES, see the file, "LICENSE.txt," in this distribution.
 *
 * simple test case for {@link PdService}
 * 
 */

package org.puredata.android.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.service.PdPreferences;
import org.puredata.android.service.PdService;
import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.IoUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class PdTest extends Activity implements OnClickListener,
		OnEditorActionListener,
		SharedPreferences.OnSharedPreferenceChangeListener {
	
	//testa commit

	private static final String TAG = "Pd Test";
	
	private float sliderVal;
	private ImageView play;
	private EditText msg;
	private TextView logs;
	private SeekBar frequency;
	private Button recordButton;
	private Button stopButton;
	
	private Thread audioThread = null;
	//private AudioRecord recorder = null;
	private boolean isRecording;
	private int samplerate;
	private short[] buffer = new short[4096];

	private PdService pdService = null;

	private Toast toast = null;

	/*
	 * private void toast(final String msg) { runOnUiThread(new Runnable() {
	 * 
	 * @Override public void run() { if (toast == null) { toast =
	 * Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT); }
	 * toast.setText(TAG + ": " + msg); toast.show(); } }); }
	 */

	private void post(final String s) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				logs.append(s + ((s.endsWith("\n")) ? "" : "\n"));
			}
		});
	}

	private PdReceiver receiver = new PdReceiver() {

		private void pdPost(String msg) {
			// toast("Pure Data says, \"" + msg + "\"");
		}

		@Override
		public void print(String s) {
			post(s);
		}

		@Override
		public void receiveBang(String source) {
			pdPost("bang");
		}

		@Override
		public void receiveFloat(String source, float x) {
			pdPost("float: " + x);
		}

		@Override
		public void receiveList(String source, Object... args) {
			pdPost("list: " + Arrays.toString(args));
		}

		@Override
		public void receiveMessage(String source, String symbol, Object... args) {
			pdPost("message: " + Arrays.toString(args));
		}

		@Override
		public void receiveSymbol(String source, String symbol) {
			pdPost("symbol: " + symbol);
		}
	};

	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder) service).getService();
			initPd();
			startAudio();

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// this method will never be called
		}
	};

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PdPreferences.initPreferences(getApplicationContext());
		PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
				.registerOnSharedPreferenceChangeListener(this);
		initGui();
		bindService(new Intent(this, PdService.class), pdConnection,
				BIND_AUTO_CREATE);
		frequency = (SeekBar) findViewById(R.id.frequency);
		OnSeekBarChangeListener listener = new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(fromUser) sliderVal = (float)progress;

			}
		};
		frequency.setOnSeekBarChangeListener(listener);
		samplerate = AudioParameters.suggestSampleRate();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cleanup();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (pdService.isRunning()) {
			startAudio();
		}
	}

	private void initGui() {
		setContentView(R.layout.main);
		play = (ImageView) findViewById(R.id.imageView1);
		play.setOnClickListener(this);
		recordButton = (Button) findViewById(R.id.recordButton);
		recordButton.setOnClickListener(this);
		stopButton = (Button) findViewById(R.id.stopButton);
		stopButton.setOnClickListener(this);
	}

	private void initPd() {
		Resources res = getResources();
		File patchFile = null;
		try {
			PdBase.setReceiver(receiver);
			PdBase.subscribe("android");
			InputStream in = res.openRawResource(R.raw.test);
			patchFile = IoUtils.extractResource(in, "test.pd", getCacheDir());
			PdBase.openPatch(patchFile);
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			finish();
		} finally {
			if (patchFile != null)
				patchFile.delete();
		}
	}
	
	private void startAudioRecording() {
//		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, samplerate,
//				AudioFormat.CHANNEL_CONFIGURATION_MONO,
//				AudioFormat.ENCODING_PCM_16BIT,
//				buffer.length);
//		recorder.startRecording();
//		recorder.read(buffer, 0, buffer.length);
//		recorder.stop();
//		recorder.release();
//		recorder = null;
//		for (int i = 0; i < buffer.length; i++) {
//			PdBase.sendFloat("micinput", buffer[i]);
//		}
	}
	
	private void stopAudioRecording() {
		
	}

	private void startAudio() {
		String name = getResources().getString(R.string.app_name);
		try {
			pdService.initAudio(-1, -1, -1, -1); // negative values will be
													// replaced with
													// defaults/preferences
			pdService.startAudio(new Intent(this, PdTest.class),
					R.drawable.icon, name, "Return to " + name + ".");
		} catch (IOException e) {
			// toast(e.toString());
		}
	}

	private void stopAudio() {
		pdService.stopAudio();
	}

	private void cleanup() {
		try {
			unbindService(pdConnection);
		} catch (IllegalArgumentException e) {
			// already unbound
			pdService = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.pd_test_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about_item:
			AlertDialog.Builder ad = new AlertDialog.Builder(this);
			ad.setTitle(R.string.about_title);
			ad.setMessage(R.string.about_msg);
			ad.setNeutralButton(android.R.string.ok, null);
			ad.setCancelable(true);
			ad.show();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageView1:
			PdBase.sendFloat("freq", sliderVal);
			PdBase.sendBang("tone");
			break;
		case R.id.frequency:
			SeekBar bar = (SeekBar) findViewById(R.id.frequency);
			PdBase.sendFloat("freq", sliderVal);
			Log.i("Max: ", Integer.toString(bar.getMax()));
			PdBase.sendBang("tone");
			break;
		case R.id.recordButton:
			Log.i("record", "tryckt record");
			startAudioRecording();
			break;
		case R.id.stopButton:
			Log.i("stop", "tryckt stop");
			stopAudioRecording();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		evaluateMessage(msg.getText().toString());
		return true;
	}

	private void evaluateMessage(String s) {
		String dest = "test", symbol = null;
		boolean isAny = s.length() > 0 && s.charAt(0) == ';';
		Scanner sc = new Scanner(isAny ? s.substring(1) : s);
		if (isAny) {
			if (sc.hasNext())
				dest = sc.next();
			else {
				// toast("Message not sent (empty recipient)");
				return;
			}
			if (sc.hasNext())
				symbol = sc.next();
			else {
				// toast("Message not sent (empty symbol)");
			}
		}
		List<Object> list = new ArrayList<Object>();
		while (sc.hasNext()) {
			if (sc.hasNextInt()) {
				list.add(new Float(sc.nextInt()));
			} else if (sc.hasNextFloat()) {
				list.add(sc.nextFloat());
			} else {
				list.add(sc.next());
			}
		}
		if (isAny) {
			PdBase.sendMessage(dest, symbol, list.toArray());
		} else {
			switch (list.size()) {
			case 0:
				PdBase.sendBang(dest);
				break;
			case 1:
				Object x = list.get(0);
				if (x instanceof String) {
					PdBase.sendSymbol(dest, (String) x);
				} else {
					PdBase.sendFloat(dest, (Float) x);
				}
				break;
			default:
				PdBase.sendList(dest, list.toArray());
				break;
			}
		}
	}
}
