package com.everyleaf.android;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class PresentationRouletteActivity extends Activity {
	@SuppressWarnings("unused")
	private final String TAG = this.getClass().getSimpleName();

	private String[] TEXTS = { "Red", "Green", "Blue", "Purple", "Yellow" };
	private ArrayList<String> current_texts = new ArrayList<String>();
	private ArrayList<String> finished_texts = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		setTypeface();
	}

	private void setTypeface() {
		Typeface tf = Typeface
				.createFromAsset(getAssets(), "JohnHancockCP.otf");
		TextView text = (TextView) findViewById(R.id.result_text);
		text.setTypeface(tf);
		text = (TextView) findViewById(R.id.finished_texts);
		text.setTypeface(tf);
		Button button = (Button) findViewById(R.id.go);
		button.setTypeface(tf);
		button = (Button) findViewById(R.id.reset);
		button.setTypeface(tf);
	}

	public void go(View view) {
		if (current_texts.isEmpty()) {
			initializeKujibiki();
		}
		rouletteResultText();
	}

	private void updateResultText() {
		String result = current_texts
				.get(getRandomizeInt(current_texts.size()));
		TextView text = (TextView) findViewById(R.id.result_text);
		text.setText(result);
		finished_texts.add(current_texts.remove(current_texts.indexOf(result)));
	}

	private void enableButtons(boolean enable) {
		Button button = null;
		button = (Button) findViewById(R.id.go);
		button.setEnabled(enable);
		button = (Button) findViewById(R.id.reset);
		button.setEnabled(enable);
	}

	private void rouletteResultText() {
		new AsyncTask<String, Integer, String>() {
			@Override
			protected void onPreExecute() {
				enableButtons(false);
			}

			@Override
			protected String doInBackground(String... params) {
				int ROULETTE_TIME = 400;
				int STOP_TIME = 40;
				int COUNT = ROULETTE_TIME + STOP_TIME;
				int sleep = 1;
				for (int i = 0; i < COUNT; i++) {
					publishProgress(i);
					try {
						Thread.sleep(sleep);
					} catch (InterruptedException e) {
					}
					if (i > ROULETTE_TIME) {
						sleep += i - ROULETTE_TIME;
					}
				}
				return "";
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				TextView text = (TextView) findViewById(R.id.result_text);
				text.setText(current_texts.get(values[0] % current_texts.size()));
			}

			@Override
			protected void onPostExecute(String result) {
				updateResultText();
				updateFinishedTexts();
				enableButtons(true);
				// TODO: リファクタリング
				if (current_texts.size() == 0) {
					Button button = null;
					button = (Button) findViewById(R.id.go);
					button.setEnabled(false);
				}
			}
		}.execute(new String[] {});
	}

	private void initializeKujibiki() {
		resetFields();
		prepareTexts();
	}

	private void resetFields() {
		TextView text = null;
		text = (TextView) findViewById(R.id.result_text);
		text.setText("");
		text = (TextView) findViewById(R.id.finished_texts);
		text.setText("");
	}

	private int getRandomizeInt(int size) {
		if (size == 0) {
			return 0;
		}
		return (int) (Math.random() * 10 % size);
	}

	private void prepareTexts() {
		current_texts.clear();
		for (String str : TEXTS) {
			current_texts.add(str);
		}
		finished_texts.clear();
	}

	private void updateFinishedTexts() {
		String strings = "";
		int i = 1;
		for (String str : finished_texts) {
			strings = strings + i + ". " + str + "\n";
			i++;
		}
		TextView text = (TextView) findViewById(R.id.finished_texts);
		text.setText(strings);

		// TODO: リファクタリング
		if (current_texts.size() == 1) {
			String result = current_texts.get(getRandomizeInt(current_texts
					.size()));
			finished_texts.add(current_texts.remove(current_texts
					.indexOf(result)));
			updateFinishedTexts();
		}
	}

	public void reset(View view) {
		prepareTexts();
		resetFields();
		enableButtons(true);
		TextView text = (TextView) findViewById(R.id.result_text);
		text.setText("");
		text.setText(this.getResources().getString(R.string.hello));
	}
}