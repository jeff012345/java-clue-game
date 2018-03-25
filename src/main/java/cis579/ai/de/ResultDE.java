package cis579.ai.de;

public class ResultDE {
	
	private double[] coefficients;
	private double successRate;
	private String gameGuid;

	public ResultDE() {
		// TODO Auto-generated constructor stub
	}
	
	
	public ResultDE(double[] coefficients, double successRate, String gameGuid) {
		super();
		this.coefficients = coefficients;
		this.successRate = successRate;
		this.gameGuid = gameGuid;
	}

	public double[] getCoefficients() {
		return coefficients;
	}

	public void setCoefficients(double[] coefficients) {
		this.coefficients = coefficients;
	}

	public double getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(double successRat) {
		this.successRate = successRat;
	}

	public String getGameGuid() {
		return gameGuid;
	}

	public void setGameGuid(String gameGuid) {
		this.gameGuid = gameGuid;
	}

}
