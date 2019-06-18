package net.thomas.kata.patterns.concurrency;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class ActiveObject {

	private static final int CENTS_PR_DOLLAR = 100;
	private long balanceInCents;

	private final Queue<Runnable> pendingTransactions;
	private final ExecutorService executor;

	public ActiveObject() {
		balanceInCents = 0;
		pendingTransactions = new LinkedBlockingQueue<>();
		executor = newSingleThreadExecutor();
		executor.execute(() -> {
			while (true) {
				if (!pendingTransactions.isEmpty()) {
					pendingTransactions.poll().run();
				}
			}
		});
	}

	public void deposit(CurrencyAmount amount) {
		pendingTransactions.add(() -> {
			balanceInCents += amount.dollars * CENTS_PR_DOLLAR + amount.cents;
		});
	}

	public void withdraw(CurrencyAmount amount) {
		pendingTransactions.add(() -> {
			balanceInCents -= amount.dollars * CENTS_PR_DOLLAR + amount.cents;
		});
	}

	public Future<CurrencyAmount> checkBalance() {
		final CompletableFuture<CurrencyAmount> balanceCheck = new CompletableFuture<>();
		pendingTransactions.add(() -> {
			balanceCheck.complete(new CurrencyAmount(balanceInCents / CENTS_PR_DOLLAR, (int) (balanceInCents % CENTS_PR_DOLLAR)));
		});
		return balanceCheck;
	}

	public void closeAccount() {
		executor.shutdown();
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		final ActiveObject account = new ActiveObject();
		account.deposit(new CurrencyAmount(100, 32));
		account.withdraw(new CurrencyAmount(23, 15));
		System.out.println(account.checkBalance().get());
	}
}

class CurrencyAmount {
	public final long dollars;
	public final int cents;

	public CurrencyAmount(long dollars, int cents) {
		this.dollars = dollars;
		this.cents = cents;
	}

	@Override
	public String toString() {
		return dollars + "." + cents;
	}
}