// =====================================================================================
// Learning Objective:
// This tutorial demonstrates how to build a lightweight, thread-safe
// event bus in Java. An event bus is a common design pattern used
// for decoupling components in an application. It allows different
// parts of your application to communicate with each other without
// needing direct references, promoting flexibility and maintainability.
//
// We will focus on the core concepts:
// 1. Event types: How to represent different kinds of events.
// 2. Subscribers: How components register to listen for specific events.
// 3. Publisher: How events are published to the bus.
// 4. Thread Safety: Ensuring safe concurrent access to the bus.
// =====================================================================================

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

// A simple interface to represent any type of event.
// This allows us to have diverse event types without a common base class constraint.
interface Event {}

// The core EventBus class.
// This class manages the registration of subscribers and the dispatching of events.
public class EventBus {

    // A map to hold subscribers. The key is the type of Event,
    // and the value is a list of Consumers that will be notified for that event type.
    // We use CopyOnWriteArrayList because it's thread-safe for concurrent modifications
    // (adding/removing subscribers) and reads (iterating to dispatch events).
    // This is a common and efficient choice for observer patterns.
    private final Map<Class<? extends Event>, List<Consumer<? extends Event>>> subscribers = new HashMap<>();

    // Private constructor to enforce singleton-like access if desired,
    // though for this example, we'll create instances directly.
    // If you wanted a global event bus, you'd add a static getInstance() method here.

    // Registers a subscriber for a specific event type.
    // The 'eventType' is the Class object of the event we want to listen for.
    // The 'subscriber' is a Consumer that will be executed when an event of 'eventType' is published.
    public <T extends Event> void subscribe(Class<T> eventType, Consumer<T> subscriber) {
        // Get the list of subscribers for this event type, or create a new list if none exist.
        // computeIfAbsent is a convenient way to handle this conditional creation in a thread-safe manner.
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                   .add(subscriber); // Add the subscriber to the list.
    }

    // Unregisters a subscriber for a specific event type.
    // This is important for preventing memory leaks and ensuring that
    // old subscribers don't receive events after they are no longer interested.
    public <T extends Event> void unsubscribe(Class<T> eventType, Consumer<T> subscriber) {
        List<Consumer<? extends Event>> subscriberList = subscribers.get(eventType);
        if (subscriberList != null) {
            subscriberList.remove(subscriber); // Remove the subscriber from the list.
        }
    }

    // Publishes an event to all registered subscribers for its type.
    // The 'event' object is the actual event instance being sent.
    public void publish(Event event) {
        // Get the class of the event. This will be used as the key in our subscribers map.
        Class<? extends Event> eventType = event.getClass();

        // Retrieve the list of subscribers for this event type.
        List<Consumer<? extends Event>> subscriberList = subscribers.get(eventType);

        // If there are subscribers for this event type, iterate through them and notify them.
        if (subscriberList != null) {
            for (Consumer<? extends Event> subscriber : subscriberList) {
                // Safely cast the subscriber and call its accept method.
                // The cast is safe because we've stored consumers of specific types
                // for their corresponding event types.
                try {
                    @SuppressWarnings("unchecked") // Suppress warning as we know the type is compatible.
                    Consumer<T> typedSubscriber = (Consumer<T>) subscriber;
                    typedSubscriber.accept((T) event); // Pass the event to the subscriber.
                } catch (ClassCastException e) {
                    // This should ideally not happen if the subscribe method is used correctly,
                    // but it's a good defensive programming practice.
                    System.err.println("ClassCastException during event dispatch: " + e.getMessage());
                } catch (Exception e) {
                    // Catch any exceptions thrown by the subscriber to prevent
                    // one faulty subscriber from stopping other notifications.
                    System.err.println("Exception in subscriber for event " + eventType.getSimpleName() + ": " + e.getMessage());
                    e.printStackTrace(); // Log the full stack trace for debugging.
                }
            }
        }
    }

    // =====================================================================================
    // Example Usage:
    // =====================================================================================

    // Define some sample event classes. They just need to implement the Event interface.
    static class UserLoggedInEvent implements Event {
        private final String username;
        UserLoggedInEvent(String username) { this.username = username; }
        public String getUsername() { return username; }
    }

    static class OrderPlacedEvent implements Event {
        private final String orderId;
        OrderPlacedEvent(String orderId) { this.orderId = orderId; }
        public String getOrderId() { return orderId; }
    }

    public static void main(String[] args) {
        // Create an instance of our EventBus.
        EventBus eventBus = new EventBus();

        // --- Subscriber 1: Logs user login events ---
        // Define a consumer that will be notified when a UserLoggedInEvent occurs.
        Consumer<UserLoggedInEvent> loginLogger = event -> {
            System.out.println("Login Logger: User '" + event.getUsername() + "' logged in.");
        };
        // Subscribe the loginLogger to UserLoggedInEvent.
        eventBus.subscribe(UserLoggedInEvent.class, loginLogger);

        // --- Subscriber 2: Sends welcome email on user login ---
        // Define another consumer for UserLoggedInEvent.
        Consumer<UserLoggedInEvent> welcomeEmailSender = event -> {
            System.out.println("Welcome Email Sender: Sending welcome email to '" + event.getUsername() + "'.");
        };
        // Subscribe the welcomeEmailSender to UserLoggedInEvent.
        eventBus.subscribe(UserLoggedInEvent.class, welcomeEmailSender);

        // --- Subscriber 3: Logs order placed events ---
        Consumer<OrderPlacedEvent> orderLogger = event -> {
            System.out.println("Order Logger: Order '" + event.getOrderId() + "' placed.");
        };
        // Subscribe the orderLogger to OrderPlacedEvent.
        eventBus.subscribe(OrderPlacedEvent.class, orderLogger);

        // --- Publishing Events ---

        System.out.println("\n--- Publishing User Login Events ---");
        // Publish a UserLoggedInEvent. Both loginLogger and welcomeEmailSender will be notified.
        eventBus.publish(new UserLoggedInEvent("Alice"));
        eventBus.publish(new UserLoggedInEvent("Bob"));

        System.out.println("\n--- Publishing Order Placed Events ---");
        // Publish an OrderPlacedEvent. Only orderLogger will be notified.
        eventBus.publish(new OrderPlacedEvent("ORD-12345"));

        System.out.println("\n--- Unsubscribing one login logger ---");
        // Unsubscribe the welcomeEmailSender. It will no longer receive UserLoggedInEvents.
        eventBus.unsubscribe(UserLoggedInEvent.class, welcomeEmailSender);

        System.out.println("\n--- Publishing another User Login Event after unsubscribe ---");
        // Publish another UserLoggedInEvent. Only loginLogger will be notified now.
        eventBus.publish(new UserLoggedInEvent("Charlie"));

        System.out.println("\n--- Finished ---");
    }
}
// =====================================================================================
// Key Takeaways for Beginners:
//
// 1. Decoupling: The EventBus allows components (like the "Login Logger" and
//    "Welcome Email Sender") to interact without knowing about each other directly.
//    They only know about the EventBus and the Event types. This makes it easy to
//    add, remove, or modify components without affecting others.
//
// 2. Publish-Subscribe Pattern: This is a classic implementation of the
//    publish-subscribe (pub/sub) pattern. Publishers send messages (events),
//    and subscribers receive them if they've expressed interest.
//
// 3. Generics (`<T extends Event>`, `Class<T>`): Generics are used to ensure type
//    safety. When you subscribe, you specify the exact type of event you're
//    interested in, and the compiler helps ensure you handle it correctly.
//
// 4. Consumer Functional Interface: `java.util.function.Consumer<T>` is a
//    functional interface that takes one argument and returns no result. It's
//    perfect for representing actions to be performed when an event occurs.
//
// 5. Thread Safety (`CopyOnWriteArrayList`): In multi-threaded applications,
//    multiple threads might try to subscribe, unsubscribe, or publish events
//    simultaneously. `CopyOnWriteArrayList` is used here because it handles
//    these concurrent operations safely without requiring explicit `synchronized`
//    blocks around list access, making the EventBus robust.
//
// 6. Exception Handling: The `try-catch` block around `subscriber.accept(event)`
//    is crucial. If one subscriber throws an error, it won't stop other
//    subscribers from receiving the event. This makes the event bus more
//    resilient.
//
// This lightweight event bus can be extended for more complex scenarios,
// such as asynchronous event processing (using thread pools) or event filtering.
// =====================================================================================