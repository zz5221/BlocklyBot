package com.tharvey.blocklybot;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

public class Speak implements IFunction {
	private final static String TAG = Speak.class.getSimpleName();

	TextToSpeech mTTS;
	String mSpeaking;

	public Speak(Activity context) {
		mSpeaking = null;
		mTTS = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if (status == TextToSpeech.ERROR) {
					Log.e(TAG, "TTS init failed");
				} else if (status == TextToSpeech.SUCCESS) {
					mTTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {
						@Override
						public void onDone(String utteranceId) {
							Log.i(TAG, "TTS complete '" + mSpeaking + "'");
							mTTS.stop();
							mSpeaking = null;
						}

						@Override
						public void onError(String utteranceId) {
							Log.e(TAG, "TTS error");
						}

						@Override
						public void onStart(String utteranceId) {
							Log.i(TAG, "TTS start");
						}
					});

					// TODO: select language from preferences or can I get it from global?
					int result = mTTS.setLanguage(Locale.US);
					if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
						Log.e(TAG, "This Language is not supported");
					}
				}
			}
		});
	}

	public boolean isBusy() {
		return (mSpeaking != null);
	}

	public boolean doFunction(String txt, int p2, int p3) {
		Log.i(TAG, "Speaking '" + txt + "'");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
		mSpeaking = txt;
		mTTS.speak(txt, TextToSpeech.QUEUE_ADD, params);
		return true;
	}
}
