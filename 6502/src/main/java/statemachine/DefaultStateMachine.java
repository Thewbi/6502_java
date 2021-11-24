package statemachine;

public class DefaultStateMachine implements StateMachine {
	
	private State state = State.UNKNOWN;

	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

}
