package produto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/cadProduto")
public class CadProdutoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public CadProdutoServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		// Pegando os dados do formulário HTML pelo request
		try {
			String nome = request.getParameter("nome");
			String fabricante = request.getParameter("fabricante");
			String categoria = request.getParameter("categoria");
			int numero_serie = Integer.parseInt(request.getParameter("numerodeserie"));
			boolean fragile = request.getParameter("fragile") != null;
			// Gerando o JSON para enviar ao webservice
			JSONObject json = new JSONObject();
			json.put("nome", nome);
			json.put("fabricante", fabricante);
			json.put("categoria", categoria);
			json.put("numero_serie", numero_serie);
			json.put("fragil", fragile);
			// Definindo o endpoint (URL) do web service
			URL url = new URL("http://localhost/estoquegerenciamento/cadproduto.php");
			// Criando o objeto para conexão HTTP
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// Configurando a conexão
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json; utf-8");
			conn.setRequestProperty("Accept", "application/json");
			conn.setDoOutput(true);
			try {
				// Enviando o json gerado pelo request
				OutputStream os = conn.getOutputStream();
				byte[] input = json.toString().getBytes("utf-8");
				os.write(input, 0, input.length);
				// recebendo a resposta (response) do web service
				StringBuilder responseContent = new StringBuilder();
				InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "utf-8");
				BufferedReader br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null) {
					responseContent.append(line.trim());
				}
				// Enviando response para o cliente http
				response.setContentType("application/json");
				response.getWriter().write(responseContent.toString());
			} catch (Error e) {
				System.out.println(e.getMessage());
			}

		} catch (Exception e) {
			System.out.print(e.getMessage());
		}

	}

}
