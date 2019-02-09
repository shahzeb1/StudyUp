package edu.studyup.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.studyup.entity.Event;
import edu.studyup.entity.Location;
import edu.studyup.entity.Student;
import edu.studyup.util.DataStorage;
import edu.studyup.util.StudyUpException;

class EventServiceImplTest {

	EventServiceImpl eventServiceImpl;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		eventServiceImpl = new EventServiceImpl();
		//Create Student
		Student student = new Student();
		student.setFirstName("John");
		student.setLastName("Doe");
		student.setEmail("JohnDoe@email.com");
		student.setId(1);
		
		//Create Event1
		Event event = new Event();
		event.setEventID(1);
		event.setDate(new Date());
		event.setName("Event 1");
		Location location = new Location(-122, 37);
		event.setLocation(location);
		List<Student> eventStudents = new ArrayList<>();
		eventStudents.add(student);
		event.setStudents(eventStudents);
		
		DataStorage.eventData.put(event.getEventID(), event);
	}

	@AfterEach
	void tearDown() throws Exception {
		DataStorage.eventData.clear();
	}

	@Test
	void testUpdateEventName_goodCase() throws StudyUpException {
		int eventID = 1;
		eventServiceImpl.updateEventName(eventID, "Renamed Event 1");
		assertEquals("Renamed Event 1", DataStorage.eventData.get(eventID).getName());
	}
	
	@Test
	void testUpdateEvent_WrongEventID_badCase() {
		int eventID = 3;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Renamed Event 3");
		  });
	}
	
	// Start of the tests written for HW 2
	
	@Test
	void testUpdateEventName_length_goodCase() throws StudyUpException {
		// This test makes sure that when the name of the new event is = 20 chars
		// it should not throw an error since maximum allowed length for a 
		// new eventName is 20 chars = 20 is good
		
		int eventID = 1;
		String longName = "ABCDEFGHIJKLMNOPQRST";
		
		// Just assert that the length of longName is 20
		assertEquals(20, longName.length());
		
		// Make sure that updateEventName does not throw any error
		assertDoesNotThrow(() -> 
				{eventServiceImpl.updateEventName(eventID, longName);
			});
		
		// The event name should have been successfully updated
		assertEquals(longName, DataStorage.eventData.get(eventID).getName());
	}
	
	@Test
	void testUpdateEventName_length_badCase() throws StudyUpException {
		// This test makes sure that when the name of the new event is > 20 chars
		// it should  throw an error since maximum allowed length for a 
		// new eventName is 20 chars.
		
		int eventID = 1;
		String longName = "ABCDEFGHIJKLMNOPQRSTU";
		
		// Just assert that the length of longName is 21
		assertEquals(21, longName.length());
		
		// Make sure that updateEventName does throw any error
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, longName);
		  });
		
		// The event name should not have been updated
		assertEquals("Event 1", DataStorage.eventData.get(eventID).getName());
	}
	
	
	@Test
	void testaddStudentToEvent_presentStudents() throws StudyUpException {
		// Params for our student
		String firstName = "Barack";
		String lastName = "Obama";
		String email = "obama@hotmail.com";
		int id = 2;
		
		//Create Student
		Student student = new Student();
		student.setFirstName(firstName);
		student.setLastName(lastName);
		student.setEmail(email);
		student.setId(id);
		
		// Add this new student to event 1
		int eventId = 1;
		Event ret_event = eventServiceImpl.addStudentToEvent(student, eventId);
		// Now event 1 should have 2 students (John, Obama)
		
		assertEquals(eventId, ret_event.getEventID());
		
		List<Event> allEvents = eventServiceImpl.getActiveEvents();
		assertEquals(eventId, allEvents.get(0).getEventID());
		assertEquals("Event 1", allEvents.get(0).getName());
		
		// There should only be 1 event in the list, since we added our 
		// student to an event which already exists in the list
		assertEquals(1, allEvents.size());
		
		List<Student> allStudents = ret_event.getStudents();
		
		// Make sure we have 2 students in this event
		assertEquals(2, allStudents.size());
		
		// Make sure that Obama's information was set on index 1 of this
		// event.
		// Keep in mind that id 0 is John from the @BeforeEach function
		assertEquals(firstName, allStudents.get(1).getFirstName());
		assertEquals(lastName, allStudents.get(1).getLastName());
		assertEquals(email, allStudents.get(1).getEmail());
		assertEquals(id, allStudents.get(1).getId());
				
	}
	
	@Test
	void testaddStudentToEvent_noStudents() throws StudyUpException {
		// Create a new event with no students
		int eventId = 2;
		Event event = new Event();
		event.setEventID(eventId);
		event.setDate(new Date());
		event.setName("Event 2");
		Location location = new Location(-100, 100);
		event.setLocation(location);
		DataStorage.eventData.put(event.getEventID(), event);
		
		// Params for our student
		String firstName = "Barack";
		String lastName = "Obama";
		String email = "obama@hotmail.com";
		int id = 2;
		
		//Create Student
		Student student = new Student();
		student.setFirstName(firstName);
		student.setLastName(lastName);
		student.setEmail(email);
		student.setId(id);
		
		// Add this new student to event 1
		Event ret_event = eventServiceImpl.addStudentToEvent(student, eventId);
		
		// Make sure Event 2 was properly added to the events list
		List<Event> allEvents = eventServiceImpl.getActiveEvents();
		assertEquals(eventId, allEvents.get(1).getEventID());
		assertEquals("Event 2", allEvents.get(1).getName());
		
		// Check to make sure Obama is the only student in our event
		assertEquals(1, ret_event.getStudents().size());
	}
	
	@Test
	void testgetActiveEvents_pastDate() throws StudyUpException {
		// The getActiveEvents function should only return back events
		// that are in the future
		Date dateInPast = new Date();
		dateInPast.setTime(0); // Sets date to 1969
		
		// Insert an Event 2 which is in the past (in the year 1969)
		int eventId = 2;
		Event event = new Event();
		event.setEventID(eventId);
		event.setDate(dateInPast);
		event.setName("Event 2");
		Location location = new Location(-100, 100);
		event.setLocation(location);
		DataStorage.eventData.put(event.getEventID(), event);
		
		// Check and ensure that there should only be 0 events in
		// getActiveEvents because both Event 1 and Event 2 are 
		// created in the past
		assertEquals(0, eventServiceImpl.getActiveEvents().size());
	}
	
	@Test
	void testgetPastEvents_pastDate() throws StudyUpException {
		// Completely delete all events (there should be no Event 1)
		eventServiceImpl.deleteEvent(1);
		
		// Get the past events for when the date is in the past
		Date dateInPast = new Date();
		dateInPast.setTime(0); // Sets date to 1969
		
		// Insert an Event 2 which is in the past (in the year 1969)
		int eventId = 2;
		Event event = new Event();
		event.setEventID(eventId);
		event.setDate(dateInPast);
		event.setName("Event 2");
		Location location = new Location(-100, 100);
		event.setLocation(location);
		DataStorage.eventData.put(event.getEventID(), event);
		
		// Check and ensure that there should only 2 events in
		// getActiveEvents. 
		// 1 = Event 1, 2 = Event 2
		assertEquals(1, eventServiceImpl.getPastEvents().size());
	}
	
	@Test
	void testdeleteEvent_goodCase() throws StudyUpException {
		// Just delete the Event 1 which is created in @BeforeEach
		Event ret_event = eventServiceImpl.deleteEvent(1);
		
		// Make sure that the correct Event 1 was the deleted item
		assertEquals("Event 1", ret_event.getName());
		
		// Make sure that now the length of the number of past and future
		// events is 0
		int total_events_count = eventServiceImpl.getActiveEvents().size()
				+ eventServiceImpl.getPastEvents().size();
		assertEquals(0, total_events_count);
	}
}
