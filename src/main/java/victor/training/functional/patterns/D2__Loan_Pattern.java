package victor.training.functional.patterns;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.stream.Stream;

import org.jooq.lambda.Unchecked;
import org.springframework.data.jpa.repository.JpaRepository;

import lombok.extern.slf4j.Slf4j;

// export all orders to a file

interface OrderRepo extends JpaRepository<Order, Long> { // Spring Data FanClub
	Stream<Order> findByActiveTrue(); // 1 Mln orders ;)
}
@Slf4j
class OrderExporter {
	
	private OrderRepo orderRepo;
	
	public File exportFile(String fileName) throws Exception {
		File file = new File("export/" + fileName);
		try (Writer writer = new FileWriter(file)) {
			writeContent(writer);
			return file;
		} catch (Exception e) {
			// TODO send email notification
			log.debug("Gotcha!", e); // TERROR-Driven Development
			throw e;
		}
	}

	protected void writeContent(Writer writer) throws IOException {
		writer.write("OrderID;Date\n");
		orderRepo.findByActiveTrue()
			.map(o -> o.getId() + ";" + o.getCreationDate())
			.forEach(Unchecked.consumer(writer::write));
	}
}

class UserExporter extends OrderExporter {

	private UserRepo userRepo; 
	
	protected void writeContent(Writer writer) throws IOException {
		writer.write("Username;FirstName;LastName\n");
		userRepo.findAll().stream()
			.map(u -> u.getUsername() + ";" + u.getFirstName() + ";" + u.getLastName())
			.forEach(Unchecked.consumer(writer::write));
	}
}