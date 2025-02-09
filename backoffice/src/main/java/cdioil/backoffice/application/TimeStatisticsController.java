package cdioil.backoffice.application;

import cdioil.application.domain.authz.AuthenticationAction;
import cdioil.application.domain.authz.UserAction;
import cdioil.persistence.impl.AuthenticationHistoryRepositoryImpl;
import cdioil.persistence.impl.ReviewRepositoryImpl;
import cdioil.persistence.impl.UserActionHistoryRepositoryImpl;
import cdioil.time.TimePeriod;
import java.time.LocalDateTime;

/**
 * TimeStatisticsController that serves as a controller for knowing several 
 * statistics between two dates
 * @author <a href="1160907@isep.ipp.pt">João Freitas</a>
 * @since Version 7.0 of FeedbackMonkey
 */
public final class TimeStatisticsController {
    /**
     * LocalDateTime with the time period start
     */
    private final LocalDateTime dateTimeX;
    /**
     * LocalDateTime with the time period end
     */
    private final LocalDateTime dateTimeY;
    /**
     * Builds a new TimeStatisticsController with the time period which statistics 
     * are being retrieved
     * @param dateTimeX LocalDateTime with the time period start
     * @param dateTimeY LocalDateTime with the time period end
     */
    public TimeStatisticsController(LocalDateTime dateTimeX,LocalDateTime dateTimeY){
        this.dateTimeX=dateTimeX;
        this.dateTimeY=dateTimeY;
        checkTimePeriod(dateTimeX,dateTimeY);
    }
    /**
     * Method that gets the number of surveys answered on a certain time period
     * @return Long with the number of surveys answered on a certain time period
     */
    public long getNumberOfSurveysAnswered(){
        return new UserActionHistoryRepositoryImpl()
                .getNumberOfCertainUserAction(dateTimeX,dateTimeY,UserAction.ENDED_ANSWER_SURVEY);
    }
    /**
     * Method that gets the number of valid logins on a certain time period
     * @return Long with the number of valid logins on a certain time period
     */
    public long getNumberOfValidLogins(){
        return new AuthenticationHistoryRepositoryImpl()
                .getNumberOfCertainAuthenticationAction(dateTimeX,dateTimeY
                        ,AuthenticationAction.SUCCESFUL_LOGIN);
    }
    /**
     * Method that gets the number of invalid logins on a certain time period
     * @return Long with the number of invalid logins on a certain time period
     */
    public long getNumberOfInvalidLogins(){
        return new AuthenticationHistoryRepositoryImpl()
                .getNumberOfCertainAuthenticationAction(dateTimeX,dateTimeY
                        ,AuthenticationAction.INVALID_LOGIN);
    }
    /**
     * Method that gets the average time needed to answer a certain survey 
     * on a certain time period
     * @return Double with the average time needed to answer a certain survey 
     * on a certain time period
     */
    public double getReviewsAnswerAverageTime(){
        return new ReviewRepositoryImpl().getReviewsAverageTime(dateTimeX,dateTimeY);
    }
    /**
     * Method that verifies if a certain period of time is valid or not
     * @param dateTimeX LocalDateTime with the date time of the period start
     * @param dateTimeY LocalDateTime with the date time of the period end
     */
    private void checkTimePeriod(LocalDateTime dateTimeX, LocalDateTime dateTimeY){
        TimePeriod validTimePeriod = new TimePeriod(dateTimeX,dateTimeY);
        validTimePeriod=null;
    }
}
