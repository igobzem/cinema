package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CinemaService {
    public static final int ROWS = 9;
    public static final int COLUMNS = 9;
    private static final int PRICE_HIGH = 10;
    private static final int PRICE_LOW = 8;
    private static final int LAST_HIGH = 4;
    private static final String PASSWORD = "super_secret";
    private List<SeatDTO> seats;

    public CinemaService() {
        seats = new ArrayList<>();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0 ; j < COLUMNS; j++) {
                SeatDTO dto = new SeatDTO();
                dto.setRow(i + 1);
                dto.setColumn(j + 1);
                dto.setPrice(i < LAST_HIGH ? PRICE_HIGH : PRICE_LOW);
                dto.setAvailable(true);
                seats.add(dto);
            }
        }
    }

    public ResponseEntity purchase(int row, int col){
        Map<String, Object> map = new HashMap<>();
        if (row <=0 || row > ROWS || col <=0 || col > COLUMNS) {
            map.put("error",  "The number of a row or a column is out of bounds!");
            return new ResponseEntity(map, HttpStatus.BAD_REQUEST);
        }
        SeatDTO dto  = seats.get((row - 1) * COLUMNS + col - 1);
        if (!dto.isAvailable()) {
            map.put("error", "The ticket has been already purchased!");
            return new ResponseEntity(map, HttpStatus.BAD_REQUEST);
        }
        dto.setAvailable(false);
        dto.setUuid(UUID.randomUUID());

        Map<String, Object> ticketMap = new HashMap<>();
        ticketMap.put("row", row);
        ticketMap.put("column", col);
        ticketMap.put("price", dto.getPrice());
        map.put("token", dto.getUuid());
        map.put("ticket", ticketMap);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    public ResponseEntity returnTicket(String token) {
        Map<String, Object> map = new HashMap<>();
        UUID uuid = UUID.fromString(token);
        SeatDTO dto = seats.stream().filter(e -> uuid.equals(e.getUuid())).findFirst().orElse(null);
        if (dto == null) {
            map.put("error",  "Wrong token!");
            return new ResponseEntity(map, HttpStatus.BAD_REQUEST);
        }
        dto.setAvailable(true);
        dto.setUuid(null);
        Map<String, Object> ticketMap = new HashMap<>();
        ticketMap.put("row", dto.getRow());
        ticketMap.put("column", dto.getColumn());
        ticketMap.put("price", dto.getPrice());
        map.put("returned_ticket", ticketMap);
        return new ResponseEntity(map, HttpStatus.OK);
    }
    public List<SeatDTO> getAvailable() {
        return seats.stream().filter(seat -> seat.isAvailable()).toList();
    }

    public ResponseEntity stats(String password) {
        Map<String, Object> map = new HashMap<>();
        if (!PASSWORD.equals(password)) {
            map.put("error",  "The password is wrong!");
            return new ResponseEntity(map, HttpStatus.UNAUTHORIZED);
        }
        int income = 0;
        int available = 0;
        int purchased = 0;
        for (SeatDTO dto : seats) {
            if (dto.isAvailable()) {
                available++;
            } else {
                purchased++;
                income += dto.getPrice();
            }
        }
        map.put("current_income", income);
        map.put("number_of_available_seats", available);
        map.put("number_of_purchased_tickets", purchased);
        return new ResponseEntity(map, HttpStatus.OK);
    }
}
