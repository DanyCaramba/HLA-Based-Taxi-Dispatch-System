/*
 *   Copyright 2012 The Portico Project
 *
 *   This file is part of portico.
 *
 *   portico is free software; you can redistribute it and/or modify
 *   it under the terms of the Common Developer and Distribution License (CDDL) 
 *   as published by Sun Microsystems. For more information see the LICENSE file.
 *   
 *   Use of this software is strictly AT YOUR OWN RISK!!!
 *   If something bad happens you do not have permission to come crying to me.
 *   (that goes for your lawyer as well)
 *
 */
package RegionManager;

import INIT.Variables;
import com.sun.javaws.IconUtil;
import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public class RegionManagerFederate
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	/** The number of times we will update our attributes and send an interaction */
	public static final int ITERATIONS = 20;
   // public Variables d;
	/** The sync point all federates will sync up on before starting */
	public static final String READY_TO_RUN = "ReadyToRun";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private RTIambassador rtiamb;
	private RegionManagerFederateAmbassador fedamb;  // created when we connect
	private HLAfloat64TimeFactory timeFactory; // set when we join
	protected EncoderFactory encoderFactory;     // set when we join

	// caches of handle types - set once we join a federation

	protected InteractionClassHandle clientArrivalHandle;
	protected ParameterHandle idHandle;
	protected ParameterHandle regionFromHandle;
	protected ParameterHandle regionToHandle;


	protected InteractionClassHandle taxiClientHandle;


	protected InteractionClassHandle endTravelHandle;
	protected  ParameterHandle taxiIDHandle;
	protected  ParameterHandle regionHandle;



	public RegionManager regionManager;


	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	/**
	 * This is just a helper method to make sure all logging it output in the same form
	 */
	private void log( String message )
	{
		System.out.println( "StorageFederate   : " + message );
	}

	/**
	 * This method will block until the user presses enter
	 */
	private void waitForUser()
	{
		log( " >>>>>>>>>> Press Enter to Continue <<<<<<<<<<" );
		BufferedReader reader = new BufferedReader( new InputStreamReader(System.in) );
		try
		{
			reader.readLine();
		}
		catch( Exception e )
		{
			log( "Error while waiting for user input: " + e.getMessage() );
			e.printStackTrace();
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	////////////////////////// Main Simulation Method /////////////////////////
	///////////////////////////////////////////////////////////////////////////
	/**
	 * This is the main simulation loop. It can be thought of as the main method of
	 * the federate. For a description of the basic flow of this federate, see the
	 * class level comments
	 */
	public void runFederate( String federateName ) throws Exception
	{
		/////////////////////////////////////////////////
		// 1 & 2. create the RTIambassador and Connect //
		/////////////////////////////////////////////////
		log( "Creating RTIambassador" );
		rtiamb = RtiFactoryFactory.getRtiFactory().getRtiAmbassador();
		encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();
		
		// connect
		log( "Connecting..." );
		fedamb = new RegionManagerFederateAmbassador( this );
		rtiamb.connect( fedamb, CallbackModel.HLA_EVOKED );

		//////////////////////////////
		// 3. create the federation //
		//////////////////////////////
		log( "Creating Federation..." );
		// We attempt to create a new federation with the first three of the
		// restaurant FOM modules covering processes, food and drink
		try
		{
			URL[] modules = new URL[]{
			    (new File("foms/ProducerConsumer.xml")).toURI().toURL(),
			};
			
			rtiamb.createFederationExecution( "ProducerConsumerFederation", modules );
			log( "Created Federation" );
		}
		catch( FederationExecutionAlreadyExists exists )
		{
			log( "Didn't create federation, it already existed" );
		}
		catch( MalformedURLException urle )
		{
			log( "Exception loading one of the FOM modules from disk: " + urle.getMessage() );
			urle.printStackTrace();
			return;
		}
		
		////////////////////////////
		// 4. join the federation //
		////////////////////////////
		rtiamb.joinFederationExecution( federateName,            // name for the federate
		                                "storage",   // federate type
		                                "ProducerConsumerFederation"     // name of federation
		                                 );           // modules we want to add

		log( "Joined Federation as " + federateName );
		
		// cache the time factory for easy access
		this.timeFactory = (HLAfloat64TimeFactory)rtiamb.getTimeFactory();

		////////////////////////////////
		// 5. announce the sync point //
		////////////////////////////////
		// announce a sync point to get everyone on the same page. if the point
		// has already been registered, we'll get a callback saying it failed,
		// but we don't care about that, as long as someone registered it
		rtiamb.registerFederationSynchronizationPoint( READY_TO_RUN, null );
		// wait until the point is announced
		while( fedamb.isAnnounced == false )
		{
			rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
		}

		// WAIT FOR USER TO KICK US OFF
		// So that there is time to add other federates, we will wait until the
		// user hits enter before proceeding. That was, you have time to start
		// other federates.
		waitForUser();

		///////////////////////////////////////////////////////
		// 6. achieve the point and wait for synchronization //
		///////////////////////////////////////////////////////
		// tell the RTI we are ready to move past the sync point and then wait
		// until the federation has synchronized on
		rtiamb.synchronizationPointAchieved( READY_TO_RUN );
		log( "Achieved sync point: " +READY_TO_RUN+ ", waiting for federation..." );
		while( fedamb.isReadyToRun == false )
		{
			rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
		}

		/////////////////////////////
		// 7. enable time policies //
		/////////////////////////////
		// in this section we enable/disable all time policies
		// note that this step is optional!
		enableTimePolicy();
		log( "Time Policy Enabled" );

		//////////////////////////////
		// 8. publish and subscribe //
		//////////////////////////////
		// in this section we tell the RTI of all the data we are going to
		// produce, and all the data we want to know about
		publishAndSubscribe();
		log( "Published and Subscribed" );

		/////////////////////////////////////
		// 9. register an object to update //
		/////////////////////////////////////
		//!ObjectInstanceHandle objectHandle = rtiamb.registerObjectInstance( storageHandle );
		//!log( "Registered Storage, handle=" + objectHandle );

		/////////////////////////////////////
		// 10. do the main simulation loop //
		/////////////////////////////////////
		// here is where we do the meat of our work. in each iteration, we will
		// update the attribute values of the object we registered, and will
		// send an interaction.
		regionManager = RegionManager.getInstance();
		double endTime=Variables.timeEnd;
		int[] tab = new int[3];
		Taxi taxiArrival ;
		while( fedamb.isRunning )
		{
				if(fedamb.federateTime>=endTime)
				{
					System.out.println("Liczba taksówek aby nie robily sie kolejki wynosi:" +(Taxi.nextTaxiID-1));
					return;

				}

			for(int i = 0; i< regionManager.regions.size() ; i++)
			{



				while (!regionManager.regions.get(i).clientQueue.isEmpty())
				{
					ClientT clientArrival = regionManager.regions.get(i).clientQueue.poll();

					taxiArrival = regionManager.regions.get(i).anyFree();



					if (taxiArrival != null && clientArrival != null)
						{

							taxiArrival.isFree=false;

							ParameterHandleValueMap parameterHandleValueMap = rtiamb.getParameterHandleValueMapFactory().create(3);

							ParameterHandle taxiClientIdHandle = rtiamb.getParameterHandle(taxiClientHandle, "TaxiID");
							ParameterHandle taxiClientRegionFrom = rtiamb.getParameterHandle(taxiClientHandle, "RegionFrom");
							ParameterHandle taxiClientRegionTo = rtiamb.getParameterHandle(taxiClientHandle, "RegionTo");

							HLAinteger32BE taxiID = encoderFactory.createHLAinteger32BE(taxiArrival.taxiID);
							HLAinteger32BE regionFrom = encoderFactory.createHLAinteger32BE(clientArrival.regionFrom);
							HLAinteger32BE regionTo = encoderFactory.createHLAinteger32BE(clientArrival.regionTo);

							parameterHandleValueMap.put(taxiClientIdHandle, taxiID.toByteArray());
							parameterHandleValueMap.put(taxiClientRegionFrom, regionFrom.toByteArray());
							parameterHandleValueMap.put(taxiClientRegionTo, regionTo.toByteArray());

							rtiamb.sendInteraction(taxiClientHandle, parameterHandleValueMap, generateTag());

							System.out.println("(Wysylam interakcje) Dobieram taxi o id:" + taxiArrival.taxiID + "z regionu: " + clientArrival.regionFrom + " do regionu: " + clientArrival.regionTo);

						}

				}

			}

			advanceTime(1);
			log( "Time Advanced to " + fedamb.federateTime );
		}

		//////////////////////////////////////
		// 11. delete the object we created //
		//////////////////////////////////////
//		deleteObject( objectHandle );
//		log( "Deleted Object, handle=" + objectHandle );

		////////////////////////////////////
		// 12. resign from the federation //
		////////////////////////////////////
		rtiamb.resignFederationExecution( ResignAction.DELETE_OBJECTS );
		log( "Resigned from Federation" );

		////////////////////////////////////////
		// 13. try and destroy the federation //
		////////////////////////////////////////
		// NOTE: we won't die if we can't do this because other federates
		//       remain. in that case we'll leave it for them to clean up
		try
		{
			rtiamb.destroyFederationExecution( "ExampleFederation" );
			log( "Destroyed Federation" );
		}
		catch( FederationExecutionDoesNotExist dne )
		{
			log( "No need to destroy federation, it doesn't exist" );
		}
		catch( FederatesCurrentlyJoined fcj )
		{
			log( "Didn't destroy federation, federates still joined" );
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	////////////////////////////// Helper Methods //////////////////////////////
	////////////////////////////////////////////////////////////////////////////
	/**
	 * This method will attempt to enable the various time related properties for
	 * the federate
	 */
	private void enableTimePolicy() throws Exception
	{
		// NOTE: Unfortunately, the LogicalTime/LogicalTimeInterval create code is
		//       Portico specific. You will have to alter this if you move to a
		//       different RTI implementation. As such, we've isolated it into a
		//       method so that any change only needs to happen in a couple of spots 
		HLAfloat64Interval lookahead = timeFactory.makeInterval( fedamb.federateLookahead );
		
		////////////////////////////
		// enable time regulation //
		////////////////////////////
		this.rtiamb.enableTimeRegulation( lookahead );

		// tick until we get the callback
		while( fedamb.isRegulating == false )
		{
			rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
		}
		
		/////////////////////////////
		// enable time constrained //
		/////////////////////////////
		this.rtiamb.enableTimeConstrained();
		
		// tick until we get the callback
		while( fedamb.isConstrained == false )
		{
			rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
		}
	}
	
	/**
	 * This method will inform the RTI about the types of data that the federate will
	 * be creating, and the types of data we are interested in hearing about as other
	 * federates produce it.
	 */
	private void publishAndSubscribe() throws RTIexception
	{

		//subskrubuje interakcje przybycia klienta
		String iname = "HLAinteractionRoot.ClientArrival";
		clientArrivalHandle = rtiamb.getInteractionClassHandle( iname );
		rtiamb.subscribeInteractionClass(clientArrivalHandle);
		idHandle = rtiamb.getParameterHandle(clientArrivalHandle, "ClientID");
		regionFromHandle = rtiamb.getParameterHandle(clientArrivalHandle, "RegionFrom");
		regionToHandle = rtiamb.getParameterHandle(clientArrivalHandle, "RegionTo");

		//publikuje interakcje dobrania taksi do klienta - TaxiClient
		String iname2 = "HLAinteractionRoot.TaxiClient";
		taxiClientHandle = rtiamb.getInteractionClassHandle( iname2 );
		rtiamb.publishInteractionClass(taxiClientHandle);

		//subskrybuje interakcje zakonczenia podrozy
		String iname3 = "HLAinteractionRoot.EndTravel";
		endTravelHandle = rtiamb.getInteractionClassHandle( iname3 );
		rtiamb.subscribeInteractionClass(endTravelHandle);
		taxiIDHandle = rtiamb.getParameterHandle(endTravelHandle, "TaxiID");
		regionHandle = rtiamb.getParameterHandle(endTravelHandle, "RegionTo");
	}

	private void advanceTime( double timestep ) throws RTIexception
	{
		// request the advance
		fedamb.isAdvancing = true;
		HLAfloat64Time time = timeFactory.makeTime( fedamb.federateTime + timestep );
		rtiamb.timeAdvanceRequest( time );

		// wait for the time advance to be granted. ticking will tell the
		// LRC to start delivering callbacks to the federate
		while( fedamb.isAdvancing )
		{
			rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );

		}
	}

	private short getTimeAsShort()
	{
		return (short)fedamb.federateTime;
	}

	private byte[] generateTag()
	{
		return ("(timestamp) "+System.currentTimeMillis()).getBytes();
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void main( String[] args )
	{
		// get a federate name, use "exampleFederate" as default
		String federateName = "Storage";
		if( args.length != 0 )
		{
			federateName = args[0];
		}
		
		try
		{
			// run the example federate
			new RegionManagerFederate().runFederate( federateName );
		}
		catch( Exception rtie )
		{
			// an exception occurred, just log the information and exit
			rtie.printStackTrace();
		}
	}
}