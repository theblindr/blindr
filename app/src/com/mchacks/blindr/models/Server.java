package com.mchacks.blindr.models;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import com.checkin.avatargenerator.AvatarGenerator;
import com.mchacks.blindr.controllers.Controller;
import com.mchacks.blindr.models.HTTPRequest.RequestType;

public class Server {
	
	private static List<UserAuthenticatedListener> userAuthenticatedListeners = new ArrayList<UserAuthenticatedListener>();
	private static List<EventsListener> eventsListeners = new ArrayList<EventsListener>();
	private static String address = "https://blindr-backend.herokuapp.com/";
	
	public static void connect(String authenticationToken) {
		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "auth";
				Log.i("SERVER_INOFS", "ReList<E>t address: " + finalAddress);
				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("fb_token", arg0[0]));
				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.POST, data);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				String userId = "";
				Log.i("SERVER_INFOS", "Auth response's json: "+ result);
				try {
					userId = readUserToken(new StringReader(result));
					Log.i("SERVER_INFOS", "Auth response's userId: "+ userId);
					Controller.getInstance().setMyOwnUser(new User("Marc-Antoine", AvatarGenerator.generate(Controller.getInstance().getDimensionAvatar(), Controller.getInstance().getDimensionAvatar()), userId));
					for(UserAuthenticatedListener listener: userAuthenticatedListeners) {
						listener.onUserAuthenticated();
					}
				} catch (IOException e) {
					Log.i("SERVER_INFOS", "Error while authenticating.");
					e.printStackTrace();
				}
			}
			
		};
		request.execute(authenticationToken);
	}
	
	public static void getEvents() {
		
		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "events";
				Log.i("SERVER_INOFS", "Events address: " + finalAddress);
				
				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("city", arg0[1]));
				
				List<NameValuePair> headers = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("X-User-Token’", arg0[0]));
				
				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.GET, null, headers);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				List<Event> events = null;
				Log.i("SERVER_INFOS", "Events response's json: "+ result);
				try {
					events = readEvents(new StringReader(result));
					for(EventsListener listener:eventsListeners){
						listener.onEventsReceived(events);
					}
				} catch (IOException e) {
					Log.i("SERVER_INOFS", "Error while receiving events: ");
					e.printStackTrace();
				}
			}
			
		};
		
		request.execute(Controller.getInstance().getMyself().getId(), Controller.getInstance().getCity());
	}
	
	public static void sendPrivateMessage(User destination, String message) {
		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "events/message";
				Log.i("SERVER_INOFS", "Send private message address: " + finalAddress);
				
				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("dst_user", arg0[1]));
				data.add(new BasicNameValuePair("message", arg0[2]));
				
				List<NameValuePair> headers = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("X-User-Token", arg0[0]));
				
				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.POST, data, headers);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				Log.i("SERVER_INFOS", "Sent message's response: "+ result);
			}
			
		};
		request.execute(Controller.getInstance().getMyself().getId(), destination.getId(), message);
	}
	
	public static void sendPublicMessage(City destination, String message) {
		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "events/message";
				Log.i("SERVER_INOFS", "Send public message address: " + finalAddress);
				
				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("dst_city", arg0[1]));
				data.add(new BasicNameValuePair("message", arg0[2]));
				
				List<NameValuePair> headers = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("X-User-Token", arg0[0]));
				
				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.POST, data, headers);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				Log.i("SERVER_INFOS", "Sent message's response: "+ result);
			}
			
		};
		request.execute(Controller.getInstance().getMyself().getId(), destination.getId(), message);
	}
	
	public static void like(User user) {
		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "events/like";
				Log.i("SERVER_INOFS", "Like address: " + finalAddress);
				
				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("dst_user", arg0[1]));
				
				List<NameValuePair> headers = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("X-User-Token", arg0[0]));
				
				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.POST, data, headers);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				Log.i("SERVER_INFOS", "Like's response: "+ result);
			}
			
		};
		request.execute(Controller.getInstance().getMyself().getId(), user.getId());
	}
	
	public static void dislike(User user) {
		AsyncTask<String, Void, String> request = new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... arg0) {
				String finalAddress = address + "events/dislike";
				Log.i("SERVER_INOFS", "Like address: " + finalAddress);
				
				List<NameValuePair> data = new ArrayList<NameValuePair>();
				data.add(new BasicNameValuePair("dst_user", arg0[1]));
				
				List<NameValuePair> headers = new ArrayList<NameValuePair>();
				headers.add(new BasicNameValuePair("X-User-Token", arg0[0]));
				
				HTTPRequest request = new HTTPRequest(finalAddress, RequestType.POST, data, headers);
				return request.getOutput();
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				Log.i("SERVER_INFOS", "Dislike response: "+ result);
			}
			
		};
		request.execute(Controller.getInstance().getMyself().getId(), user.getId());
	}
	
	public static void getMatches() {
		// get les vieux matchs d'avant
	}
	
	public static void getUserEvents(User user) {
		// get les events avec un user en particulier
	}
	
	public static void addUserAuthenticatedListener(UserAuthenticatedListener listener) {
		userAuthenticatedListeners.add(listener);
	}
	
	public static void addEventsListener(EventsListener listener) {
		eventsListeners.add(listener);
	}
	
	private static String readUserToken(Reader in) throws IOException {
		JsonReader reader = new JsonReader(in);
		reader.beginObject();
		String id = "";
		while(reader.hasNext()){
			String name = reader.nextName();
			if (name.equals("token")) {
				id = reader.nextString();
				break;
			}
		}
		reader.endObject();
		reader.close();
		return id;
	}
	
	private static ArrayList<Event> readEvents(Reader in) throws IOException{
		ArrayList<Event> events = new ArrayList<Event>();
		JsonReader reader = new JsonReader(in);
		reader.beginArray();
		while (reader.hasNext()) {
			events.add(readEvent(reader));
		}
		reader.endArray();
		reader.close();
		return events;
	}
	
	private static Event readEvent(JsonReader reader) throws IOException {
		UUID id = null;
		String type = null;
		String destination = null;
		Timestamp timestamp = null;
		String userId = null;
		String message = null;
		
		reader.beginObject();
		while(reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				id = UUID.fromString(reader.nextString());
			} else if (name.equals("type")) {
				type = reader.nextString();
			} else if (name.equals("dest")) {
				destination = reader.nextString();
			} else if (name.equals("sent_at")) {
				timestamp = new Timestamp(reader.nextLong());
			} else if (name.equals("src")) {
				userId = reader.nextString();
			} else if (name.equals("message")) {
				message = reader.nextString();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		
		return EventBuilder.buildEvent(id, type, destination, timestamp, userId, message);
	}
	
}
