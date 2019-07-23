package ca.jrvs.apps.trading.model.view;

import ca.jrvs.apps.trading.model.domain.SecurityRow;

public class PortfolioView {
    private SecurityRow[] securityRows;

    public SecurityRow[] getSecurityRows() {
        return securityRows;
    }

    public void setSecurityRows(SecurityRow[] securityRows) {
        this.securityRows = securityRows;
    }
}
