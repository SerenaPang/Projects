
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Maintains a pair of associated locks, one for read-only operations and one
 * for writing. The read lock may be held simultaneously by multiple reader
 * threads, so long as there are no writers. The write lock is exclusive.
 *
 * @see SimpleLock
 *
 * @see Lock
 * @see ReadWriteLock
 */
public class SimpleReadWriteLock {

	/** The lock used for reading. */
	private final SimpleLock readerLock;

	/** The lock used for writing. */
	private final SimpleLock writerLock;

	/** The number of active readers. */
	private int readers;

	/** The number of active writers; */
	private int writers;

	/**
	 * 
	 */
	private final SimpleReadWriteLock lock;

	/**
	 * Initializes a new simple read/write lock.
	 */
	public SimpleReadWriteLock() {

		readerLock = new ReadLock();
		writerLock = new WriteLock();
		readers = 0;
		writers = 0;
		this.lock = this;
	}

	/**
	 * Returns the reader lock.
	 *
	 * @return the reader lock
	 */
	public SimpleLock readLock() {

		return readerLock;
	}

	/**
	 * Returns the writer lock.
	 *
	 * @return the writer lock
	 */
	public SimpleLock writeLock() {

		return writerLock;
	}

	/**
	 * Used to maintain simultaneous read operations.
	 */
	private class ReadLock implements SimpleLock {

		/**
		 * Will wait until there are no active writers in the system, and then will
		 * increase the number of active readers.
		 */
		@Override
		public void lock() {

			synchronized (lock) {
				while (writers > 0) {
					try {
						lock.wait();
					} catch (InterruptedException e) {

					}
				}
				readers++;
			}
		}

		/**
		 * Will decrease the number of active readers, and notify any waiting threads if
		 * necessary.
		 */
		@Override
		public void unlock() {

			synchronized (lock) {
				readers--;
				lock.notifyAll();
			}

		}
	}

	/**
	 * Used to maintain exclusive write operations.
	 */
	private class WriteLock implements SimpleLock {

		/**
		 * Will wait until there are no active readers or writers in the system, and
		 * then will increase the number of active writers.
		 */
		@Override
		public void lock() {

			synchronized (lock) {
				while (readers > 0 || writers > 0) {
					try {
						lock.wait();
					} catch (InterruptedException e) {

					}
				}
				writers++;
			}
		}

		/**
		 * Will decrease the number of active writers, and notify any waiting threads if
		 * necessary.
		 */
		@Override
		public void unlock() {

			synchronized (lock) {
				writers--;
				lock.notifyAll();
			}
		}
	}
}
