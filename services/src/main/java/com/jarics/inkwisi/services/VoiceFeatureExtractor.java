package com.jarics.inkwisi.services;

import org.openimaj.audio.features.MFCC;
import org.openimaj.video.xuggle.XuggleAudio;

import java.io.InputStream;

public class VoiceFeatureExtractor {
    public MFCC extract(InputStream pIs){
        XuggleAudio xa = new XuggleAudio( pIs );

        // Create the Fourier transform processor chained to the audio decoder
        final MFCC mfcc = new MFCC( xa );
        return mfcc;
//        // Create a visualisation to show our FFT and open the window now
//        final BarVisualisation bv = new BarVisualisation( 400, 200 );
//        bv.showWindow( "MFCCs" );
//
//        // Loop through the sample chunks from the audio capture thread
//        // sending each one through the feature extractor and displaying
//        // the results in the visualisation.
//        while( mfcc.nextSampleChunk() != null )
//        {
//            final double[][] mfccs = mfcc.getLastCalculatedFeatureWithoutFirst();
//            bv.setData( mfccs[0] );
//        }
    }
}
