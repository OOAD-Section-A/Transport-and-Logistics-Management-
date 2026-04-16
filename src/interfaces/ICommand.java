package interfaces;

/**
 * Interface: ICommand
 * SOLID: DIP - Dependency Inversion Principle
 * Used by Command pattern for encapsulating operations
 */
public interface ICommand {
    void execute();
    void undo();
}
