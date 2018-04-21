package cis579.ai.de;

import cis579.ai.AiPlayerManager.PlayerType;

public class ResultDE {

	private double[] coefficients;
	private double successRate;
	private String gameGuid;
	private String playerType;

	public ResultDE() {
		// TODO Auto-generated constructor stub
	}


	public ResultDE(final double[] coefficients, final double successRate, final String gameGuid, final PlayerType playerType) {
		super();
		this.coefficients = coefficients;
		this.successRate = successRate;
		this.gameGuid = gameGuid;
		this.playerType = playerType.toString();
	}

	public double[] getCoefficients() {
		return this.coefficients;
	}

	public void setCoefficients(final double[] coefficients) {
		this.coefficients = coefficients;
	}

	public double getSuccessRate() {
		return this.successRate;
	}

	public void setSuccessRate(final double successRat) {
		this.successRate = successRat;
	}

	public String getGameGuid() {
		return this.gameGuid;
	}

	public void setGameGuid(final String gameGuid) {
		this.gameGuid = gameGuid;
	}


	public String getPlayerType() {
		return this.playerType;
	}


	public void setPlayerType(final String playerType) {
		this.playerType = playerType;
	}

}
