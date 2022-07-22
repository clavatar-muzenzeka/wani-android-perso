/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package cd.clavatar.wani.vendor.nfcutil;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for creating {@link ParsedNdefMessage}s.
 */
public class NdefMessageParser {

    // Utility class
    private NdefMessageParser() {

    }

    /** Parse an NdefMessage */
    public static List<ParsedNdefRecord> parse(NdefMessage message) {
        return getRecords(message.getRecords());
    }
    
    
    public static List<String> parseText(NdefMessage message) {
        return getRecordsText(message.getRecords());
    }

    public static List<ParsedNdefRecord> getRecords(NdefRecord[] records) {
        List<ParsedNdefRecord> elements = new ArrayList<ParsedNdefRecord>();
        for (final NdefRecord record : records) {
            if (UriRecord.isUri(record)) {
                elements.add(UriRecord.parse(record));
            } else if (TextRecord.isText(record)) {
                elements.add(TextRecord.parse(record));
                
            } else if (SmartPoster.isPoster(record)) {
                elements.add(SmartPoster.parse(record));
            } else {
            	elements.add(new ParsedNdefRecord() {
            		/*
					@Override
					public View getView(Activity activity, LayoutInflater inflater, ViewGroup parent, int offset) {
				        TextView text = (TextView) inflater.inflate(R.layout.tag_text, parent, false);
				        text.setText(new String(record.getPayload()));				        
				        return text;
					}
					*/
					@Override
					public String getView() {
						// TODO Auto-generated method stub
						return new String(record.getPayload());
					}
            		
            	});
            }
            
        }
        return elements;
    }
    public static List<String> getRecordsText (NdefRecord[] records) {
        List<String> elements = new ArrayList<String>();
        for (final NdefRecord record : records) {
            if (UriRecord.isUri(record)) {
                elements.add(UriRecord.parse(record).getUri().toString());
            } else if (TextRecord.isText(record)) {
                elements.add(TextRecord.parse(record).getText().toString());
                
            } else if (SmartPoster.isPoster(record)) {
                elements.add(SmartPoster.parse(record).getTitle().getText());
            } else {
            	
            	elements.add(new String(record.getPayload()));
            	//elements.add(new ParsedNdefRecord().toString() {
            		/*
					@Override
					public View getView(Activity activity, LayoutInflater inflater, ViewGroup parent, int offset) {
				        TextView text = (TextView) inflater.inflate(R.layout.tag_text, parent, false);
				        text.setText(new String(record.getPayload()));				        
				        return text;
					}*/
            		
            	//});
            }
            
        }
        return elements;
    }
    
    
    
}
