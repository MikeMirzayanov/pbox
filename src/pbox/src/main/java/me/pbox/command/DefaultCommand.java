package me.pbox.command;

import me.pbox.option.Opts;
import me.pbox.pkg.Pkg;

/**
 * @author Mike Mirzayanov (mirzayanovmr@gmail.com)
 */
public abstract class DefaultCommand implements Command {
    private Opts opts;

    @Override
    public void run(Opts opts, String... args) {
        this.opts = new Opts(opts);

        if (args.length > 1) {
            for (String arg : args) {
                run(opts, arg);
            }
        } else {
            run(new Pkg(args[0]));
        }
    }

    abstract protected void run(Pkg pkg);

    public Opts getOpts() {
        return opts;
    }
}
