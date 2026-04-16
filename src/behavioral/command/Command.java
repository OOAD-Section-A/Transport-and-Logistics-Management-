package behavioral.command;

import interfaces.ICommand;

/**
 * Command: Command Interface
 * BEHAVIORAL PATTERN: Command Pattern
 * Base interface for all commands
 * SOLID: DIP - Dependency Inversion Principle
 */
public interface Command extends ICommand {
    @Override
    void execute();

    @Override
    void undo();
}
