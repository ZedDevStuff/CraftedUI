package dev.zeddevstuff.crafted.utils;

import javax.annotation.Nonnull;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class HotReloadable<T> implements AutoCloseable
{
	private static final List<HotReloadable<?>> instances = Collections.synchronizedList(new ArrayList<>());
	private static final Thread watchThread = Thread.startVirtualThread(HotReloadable::watchLoop);
	private static volatile boolean listen = true;
	public final Path file;
	private final Function<Path, T> parser;
	private final List<Consumer<T>> onReload = Collections.synchronizedList(new ArrayList<>());
	private T value;
	public T getValue() { return value; }
	private void setValue(T value)
	{
		this.value = value;
		synchronized (onReload)
		{
			for(var listener : onReload)
				listener.accept(value);
		}
	}

	public HotReloadable(@Nonnull Path file, Function<Path, T> parser)
	{
		if(Files.notExists(file))
			throw new IllegalArgumentException("File does not exist: " + file);
		this.file = file;
		this.parser = parser;
		try
		{
			setValue(parser.apply(file));
			startListening();
		}
		catch(Exception e)
		{
			throw new RuntimeException("Failed to load file: " + file, e);
		}
	}

	public void addListener(Consumer<T> listener)
	{
		synchronized (onReload)
		{
			listener.accept(value);
			onReload.add(listener);
		}
	}
	public void removeListener(Consumer<T> listener)
	{
		synchronized (onReload)
		{
			onReload.remove(listener);
		}
	}

	public void reload()
	{
		try
		{
			setValue(parser.apply(file));
		}
		catch(Exception e)
		{
			throw new RuntimeException("Failed to reload file: " + file, e);
		}
	}

	private boolean listening = false;
	private WatchService watchService;
	private WatchKey watchKey;
	private void startListening()
	{
		if(listening)
			return;
		try
		{
			watchService = FileSystems.getDefault().newWatchService();
			watchKey = file.getParent().register(watchService, java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY);
			synchronized (instances)
			{
				if(!instances.contains(this))
					instances.add(this);
			}
			listening = true;
		}
		catch(Exception e)
		{
			throw new RuntimeException("Failed to start watching file: " + file, e);
		}
	}

	private void stopListening()
	{
		if(!listening)
			return;
		try
		{
			watchKey.cancel();
			watchService.close();
			synchronized (instances)
			{
				instances.remove(this);
			}
			listening = false;
		}
		catch(Exception e)
		{
			throw new RuntimeException("Failed to stop watching file: " + file, e);
		}
	}
	@SuppressWarnings("BusyWait")
	private static void watchLoop()
	{
		try
		{
			while (listen)
			{
				synchronized (instances)
				{
					for (HotReloadable<?> hotReloadable : instances)
					{
						try
						{
							var watchKey = hotReloadable.watchKey;
							if (watchKey == null)
								continue;
							for (WatchEvent<?> event : watchKey.pollEvents())
							{
								if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
								{
									var changed = (Path) event.context();
									if (changed.equals(hotReloadable.file.getFileName()))
										hotReloadable.reload();
								}
							}
							if (!watchKey.reset())
								hotReloadable.stopListening();
						} catch (Exception e)
						{
							System.err.println("Error in hot reload watch loop for file: " + hotReloadable.file);
							e.printStackTrace();
						}
					}
				}
				try
				{
					Thread.sleep(100);
				} catch (InterruptedException e)
				{
					Thread.currentThread().interrupt();
					break;
				}
			}
		}
		catch(Exception e)
		{
			System.err.println("Error in hot reload watch loop");
			e.printStackTrace();
		}
	}

	public static HotReloadable<String> text(Path file)
	{
		return new HotReloadable<>(file, path -> {
			try
			{
				return new String(Files.readAllBytes(path));
			}
			catch(Exception e)
			{
				throw new RuntimeException("Failed to read file: " + path, e);
			}
		});
	}

	@Override
	public void close()
	{
		stopListening();
	}
}
