package cinema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class Controller {
    @Autowired
    CinemaService service;
    @GetMapping("/seats")
    public Object getSeats() {
        Map<String, Object> map = new HashMap();
        map.put("total_rows", CinemaService.ROWS);
        map.put("total_columns", CinemaService.COLUMNS);
        List<SeatDTO> listDto = service.getAvailable();
        List<Map<String, Object>> listMap = new ArrayList<>();
        for (SeatDTO dto : listDto) {
            Map<String, Object> seat = new HashMap<>();
            seat.put("row", dto.getRow());
            seat.put("column", dto.getColumn());
            seat.put("price", dto.getPrice());
            listMap.add(seat);
        }
        map.put("available_seats", listMap);
        return map;
    }

    @PostMapping("/purchase")
    public ResponseEntity makePurchase(@RequestBody SeatDTO dto) {
        return service.purchase(dto.getRow(), dto.getColumn());
    }
    @PostMapping("/return")
    public ResponseEntity returnTicket(@RequestBody Map<String, String> map) {
        return service.returnTicket(map.get("token"));
    }

    @PostMapping("/stats")
    public ResponseEntity stats(@RequestParam(name = "password", required = false) String password) {
        return service.stats(password);
    }


}
