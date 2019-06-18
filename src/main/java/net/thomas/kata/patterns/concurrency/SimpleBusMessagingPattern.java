package net.thomas.kata.patterns.concurrency;

import static java.util.Collections.singletonMap;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleBusMessagingPattern {
	private final Queue<Message> pendingMessages;
	private final List<Subscriber> subscribers;
	private final ExecutorService executor;
	private boolean running;

	public SimpleBusMessagingPattern() {
		pendingMessages = new LinkedBlockingQueue<>();
		subscribers = new LinkedList<>();
		executor = newSingleThreadExecutor();
		executor.execute(() -> {
			running = true;
			while (!pendingMessages.isEmpty() || running) {
				if (!pendingMessages.isEmpty()) {
					final Message message = pendingMessages.poll();
					for (final Subscriber subscriber : subscribers) {
						subscriber.receive(message);
					}
				}
				try {
					Thread.sleep(10);
				} catch (final InterruptedException e) {
				}
			}
		});
	}

	public synchronized void subscribe(Subscriber subscriber) {
		subscribers.add(subscriber);
	}

	public void publish(Message message) {
		pendingMessages.add(message);
	}

	public void shutdown() {
		running = false;
		executor.shutdown();
	}

	public void shutdownNow() {
		running = false;
		pendingMessages.clear();
		executor.shutdownNow();
	}

	public static void main(String[] args) {
		SimpleBusMessagingPattern bus = new SimpleBusMessagingPattern();
		bus.subscribe((message) -> {
			System.out.println((String) message.getValue("value"));
		});
		bus.publish(new SimpleMessage(singletonMap("value", "Hello, Messaging World!")));
		bus.shutdown();
		bus = new SimpleBusMessagingPattern();
		bus.subscribe((message) -> {
			System.out.println((String) message.getValue("value"));
		});
		bus.publish(new SimpleMessage(singletonMap("value", "Highly unlikely that anyone will ever receive this message")));
		bus.shutdownNow();
	}
}

@FunctionalInterface
interface Subscriber {
	void receive(Message mesage);
}

interface Message {
	<TYPE> TYPE getValue(String valueId);
}

class SimpleMessage implements Message {
	private final Map<String, Object> values;

	public SimpleMessage(Map<String, Object> values) {
		this.values = values;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <TYPE> TYPE getValue(String valueId) {
		if (values.containsKey(valueId)) {
			return (TYPE) values.get(valueId);
		} else {
			throw new RuntimeException("Message did not contain value for " + valueId);
		}
	}
}