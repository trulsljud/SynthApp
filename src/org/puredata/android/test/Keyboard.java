package org.puredata.android.test;

import org.puredata.core.PdBase;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class Keyboard extends Activity implements OnClickListener {
	ImageView tangent1, tangent2, tangent3, tangent4, tangent5, tangent6,
			tangent7, tangent8;
	float freqA = 440;
	float freqB = 493.883f;
	float freqC = 523.251f;
	float freqD = 587.330f;
	float freqE = 659.255f;
	float freqF = 698.456f;
	float freqG = 783.991f;
	float freqAh = 880.000f;

	public Keyboard() {
		initGui();
	}

	private void initGui() {
		setContentView(R.layout.keyboard);
		tangent1 = (ImageView) findViewById(R.id.tangent1);
		tangent1.setOnClickListener(this);
		tangent2 = (ImageView) findViewById(R.id.tangent2);
		tangent2.setOnClickListener(this);
		tangent3 = (ImageView) findViewById(R.id.tangent3);
		tangent3.setOnClickListener(this);
		tangent4 = (ImageView) findViewById(R.id.tangent4);
		tangent4.setOnClickListener(this);
		tangent5 = (ImageView) findViewById(R.id.tangent5);
		tangent5.setOnClickListener(this);
		tangent6 = (ImageView) findViewById(R.id.tangent6);
		tangent6.setOnClickListener(this);
		tangent7 = (ImageView) findViewById(R.id.tangent7);
		tangent7.setOnClickListener(this);
		tangent8 = (ImageView) findViewById(R.id.tangent8);
		tangent8.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tangent1:
			PdBase.sendFloat("freq", freqA);
			PdBase.sendBang("tone");
			break;
		case R.id.tangent2:
			PdBase.sendFloat("freq", freqB);
			PdBase.sendBang("tone");
			break;
		case R.id.tangent3:
			PdBase.sendFloat("freq", freqC);
			PdBase.sendBang("tone");
			break;
		case R.id.tangent4:
			PdBase.sendFloat("freq", freqD);
			PdBase.sendBang("tone");
			break;
		case R.id.tangent5:
			PdBase.sendFloat("freq", freqE);
			PdBase.sendBang("tone");
			break;
		case R.id.tangent6:
			PdBase.sendFloat("freq", freqF);
			PdBase.sendBang("tone");
			break;
		case R.id.tangent7:
			PdBase.sendFloat("freq", freqG);
			PdBase.sendBang("tone");
			break;
		case R.id.tangent8:
			PdBase.sendFloat("freq", freqAh);
			PdBase.sendBang("tone");
			break;
		}

	}
}
