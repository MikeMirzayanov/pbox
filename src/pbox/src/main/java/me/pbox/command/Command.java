package me.pbox.command;

import me.pbox.option.Opts;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public interface Command {
    void run(Opts opts, String ... args);
}
