package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.ResultSet;
import java.sql.Statement;

public class V16__create_admin_user extends BaseJavaMigration {

  @Override
  public void migrate(Context context) throws Exception {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String hashedPassword = encoder.encode("Mat15766@");

    try (Statement statement = context.getConnection().createStatement()) {
      String checkUser = "SELECT COUNT(*) FROM users WHERE email = 'admin@admin.com'";
      var resultSet = statement.executeQuery(checkUser);
      resultSet.next();
      int count = resultSet.getInt(1);

      if (count == 0) {
        String insertUser = String.format(
            "INSERT INTO users (email, password, role, is_active) " +
                "VALUES ('admin@admin.com', '%s', 0, '1')",
            hashedPassword);
        statement.executeUpdate(insertUser);

        String getUserId = "SELECT id FROM users WHERE email = 'admin@admin.com'";
        ResultSet userIdResult = statement.executeQuery(getUserId);
        userIdResult.next();
        Long userId = userIdResult.getLong("id");

        String getGenderId = "SELECT id FROM gender WHERE name = 'MASCULINO'";
        ResultSet genderResult = statement.executeQuery(getGenderId);
        Long genderId;
        if (genderResult.next()) {
          genderId = genderResult.getLong("id");
        } else {
          String getAnyGender = "SELECT id FROM gender LIMIT 1";
          ResultSet anyGenderResult = statement.executeQuery(getAnyGender);
          anyGenderResult.next();
          genderId = anyGenderResult.getLong("id");
        }

        String insertCustomer = String.format(
            "INSERT INTO customer (full_name, cpf, birthdate, cellphone, gender_id, user_id, created_at, updated_at) " +
                "VALUES ('Rodrigo Rocha', '00000000000', '1990-01-01', '11999999999', %d, %d, NOW(), NOW())",
            genderId, userId);
        statement.executeUpdate(insertCustomer);

        String getCustomerId = "SELECT id FROM customer WHERE user_id = " + userId;
        ResultSet customerIdResult = statement.executeQuery(getCustomerId);
        customerIdResult.next();
        Long customerId = customerIdResult.getLong("id");

        String insertAddress = String.format(
            "INSERT INTO address (title, cep, residencetype, addresstype, streetname, addressnumber, " +
                "neighborhoods, state, city, country, observations) " +
                "VALUES ('Endereço Principal', '00000000', 'Casa', 'Rua', 'Rua Administrativa', '1', " +
                "'Centro', 'SP', 'São Paulo', 'Brasil', 'Endereço administrativo')");
        statement.executeUpdate(insertAddress);

        String getAddressId = "SELECT id FROM address WHERE title = 'Endereço Principal' AND cep = '00000000'";
        ResultSet addressIdResult = statement.executeQuery(getAddressId);
        addressIdResult.next();
        Long addressId = addressIdResult.getLong("id");

        String linkAddress = String.format(
            "INSERT INTO customer_address (address_id, customer_id) VALUES (%d, %d)",
            addressId, customerId);
        statement.executeUpdate(linkAddress);
      }
    }
  }
}
